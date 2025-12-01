package elearningspringboot.service.impl;

import elearningspringboot.dto.request.ExchangeTokenRequest;
import elearningspringboot.dto.request.SignInRequest;
import elearningspringboot.dto.response.*;
import elearningspringboot.entity.Role;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.ErrorCode;
import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.TokenType;
import elearningspringboot.exception.AppException;
import elearningspringboot.exception.InvalidTokenException;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.repository.httpclient.GoogleIdentityClient;
import elearningspringboot.repository.httpclient.GoogleUserInfoClient;
import elearningspringboot.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final WhitelistTokenService whitelistTokenService;
    private final GoogleIdentityClient googleIdentityClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Value("${oauth2.google.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${oauth2.google.redirect-uri}")
    private String REDIRECT_URI;

    @Override
    public TokenResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userService.findUserByEmail(request.getEmail());
        if (user.getStatus().equals(Status.PENDING)) {
            throw new AppException(ErrorCode.PENDING_ACCOUNT);
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        whitelistTokenService.createToken(accessToken, TokenType.ACCESS_TOKEN, request.getEmail());
        whitelistTokenService.createToken(refreshToken, TokenType.REFRESH_TOKEN, request.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .gender(user.getGender())
                        .birthDate(user.getBirthDate())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .avatarUrl(user.getAvatarUrl())
                        .role(user.getRole().getRole())
                        .noPassword(user.getNoPassword())
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .build();
    }

    @Override
    public TokenResponse authenticateGoogle(String code) {
        String GRANT_TYPE = "authorization_code";
        ExchangeTokenRequest request = ExchangeTokenRequest.builder()
                .code(code)
                .redirectUri(REDIRECT_URI)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .grantType(GRANT_TYPE)
                .build();
        ExchangeTokenResponse exchangeTokenResponse = googleIdentityClient.getToken(request);
        GoogleUserInfoResponse userInfoResponse = googleUserInfoClient
                .getUserInfo("Bearer " + exchangeTokenResponse.getAccessToken());
        User userResponse;
        if (!userRepository.existsByEmail(userInfoResponse.getEmail())) {
            Role role = roleService.findRoleByRoleName("USER");
            User user = User.builder()
                    .email(userInfoResponse.getEmail())
                    .fullName(userInfoResponse.getName())
                    .avatarUrl(userInfoResponse.getPicture())
                    .status(Status.ACTIVE)
                    .role(role)
                    .gender(Gender.MALE)
                    .noPassword(true)
                    .build();
            userResponse = userRepository.save(user);
        } else {
            userResponse = userService.findUserByEmail(userInfoResponse.getEmail());
            if (userResponse.getStatus().equals(Status.PENDING)) {
                userResponse.setStatus(Status.ACTIVE);
            }
        }
        String accessToken = jwtService.generateAccessToken(userResponse);
        String refreshToken = jwtService.generateRefreshToken(userResponse);

        whitelistTokenService.createToken(accessToken, TokenType.ACCESS_TOKEN, userInfoResponse.getEmail());
        whitelistTokenService.createToken(refreshToken, TokenType.REFRESH_TOKEN, userInfoResponse.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.builder()
                        .id(userResponse.getId())
                        .birthDate(userResponse.getBirthDate())
                        .phoneNumber(userResponse.getPhoneNumber())
                        .address(userResponse.getAddress())
                        .gender(userResponse.getGender())
                        .fullName(userResponse.getFullName())
                        .email(userResponse.getEmail())
                        .avatarUrl(userResponse.getAvatarUrl())
                        .role(userResponse.getRole().getRole())
                        .noPassword(userResponse.getNoPassword())
                        .status(userResponse.getStatus())
                        .createdAt(userResponse.getCreatedAt())
                        .updatedAt(userResponse.getUpdatedAt())
                        .build())
                .build();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidTokenException();
        }
        String email = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);

        UserDetails user = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH_TOKEN)
                || !whitelistTokenService.existsByToken(refreshToken)) {
            throw new InvalidTokenException();
        }
        String newAccessToken = jwtService.generateAccessToken(user);

        whitelistTokenService.createToken(newAccessToken, TokenType.ACCESS_TOKEN, email);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void signOut(String accessToken, String refreshToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            whitelistTokenService.deleteByToken(accessToken);
        }

        if (StringUtils.isNotBlank(refreshToken)) {
            String email = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);
            UserDetails user = userDetailsService.loadUserByUsername(email);

            if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH_TOKEN)) {
                throw new InvalidTokenException();
            }

            if (!whitelistTokenService.existsByToken(refreshToken)) {
                throw new InvalidTokenException();
            }
            whitelistTokenService.deleteByToken(refreshToken);
        }
    }

}
