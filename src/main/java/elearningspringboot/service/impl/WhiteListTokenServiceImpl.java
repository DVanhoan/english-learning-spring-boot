package elearningspringboot.service.impl;

import elearningspringboot.entity.WhitelistToken;
import elearningspringboot.enumeration.TokenType;
import elearningspringboot.repository.WhitelistTokenRepository;
import elearningspringboot.service.WhitelistTokenService;
import elearningspringboot.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class WhiteListTokenServiceImpl implements WhitelistTokenService {

    private final WhitelistTokenRepository whitelistTokenRepository;
    private final JwtService jwtService;

    @Override
    public void createToken(String token, TokenType tokenType, String email) {
        Date expiredTime = jwtService.extractExpiration(token, tokenType);
        LocalDateTime localDateTime = expiredTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        WhitelistToken whitelistToken = WhitelistToken.builder()
                .token(token)
                .email(email)
                .expiredTime(localDateTime)
                .tokenType(tokenType)
                .build();
        whitelistTokenRepository.save(whitelistToken);
    }

    @Override
    public void deleteByToken(String token) {
        whitelistTokenRepository.deleteByToken(token);
    }

    public boolean existsByToken(String token) {
        return whitelistTokenRepository.existsByToken(token);
    }

}
