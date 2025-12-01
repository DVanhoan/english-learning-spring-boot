package elearningspringboot.service.impl;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.UserResponse;
import elearningspringboot.entity.Role;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.ErrorCode;
import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.TokenType;
import elearningspringboot.exception.AppException;
import elearningspringboot.exception.InvalidTokenException;
import elearningspringboot.exception.ResourceConflictException;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.UserMapper;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.*;
import elearningspringboot.util.AppUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final MessageSource messageSource;
    private final MailService mailService;
    private final JwtService jwtService;
    private final WhitelistTokenService whitelistTokenService;

    @Override
    public UserResponse createUser(MultipartFile avatar, AdminUserRequest request) {
        log.info("Admin creating new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Cannot create user. Email '{}' already exists", request.getEmail());
            String message = messageSource.getMessage("user.email.exists", null, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        User user = userMapper.fromAdminUserRequestToEntity(request);
        Role role = roleService.findRoleByRoleName(request.getRole());
        user.setGender(Gender.getGenderFromName(request.getGender()));
        user.setRole(role);
        user.setStatus(Status.getStatusFromName(request.getStatus()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNoPassword(false);
        if (avatar != null && !avatar.isEmpty()) {
            log.info("Uploading avatar");
            String avatarUrl = storageService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
            log.info("Upload avatar successfully");
        }
        userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getId());
        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    public UserResponse registerUser(UserRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Cannot register user. Email '{}' already exists", request.getEmail());
            String message = messageSource.getMessage("user.email.exists.with.email",
                    new Object[] { request.getEmail() }, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        User user = userMapper.fromUserRequestToEntity(request);
        Role role = roleService.findRoleByRoleName("USER");
        user.setRole(role);
        user.setStatus(Status.PENDING);
        user.setGender(Gender.getGenderFromName(request.getGender()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNoPassword(false);
        userRepository.save(user);
        mailService.sendConfirmLink(user);

        log.info("User registered successfully with ID: {}", user.getId());
        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        User user = findUserById(id);
        log.info("Found user with ID: {}", id);

        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<UserResponse>> getUsersWithPaginationAndKeyword(int pageNumber, int pageSize,
            List<String> sorts, String keyword) {
        log.info("Fetching users with pagination: pageNumber={}, pageSize={}", pageNumber, pageSize);

        List<String> whiteListFieldSorts = List.of("fullName", "email", "role", "status", "createdAt", "updatedAt");
        Page<User> userPage = userRepository.searchUsers(keyword.toLowerCase(),
                AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize));

        List<UserResponse> userResponses = userPage.getContent()
                .stream()
                .map(user -> {
                    UserResponse userResponse = userMapper.toDTO(user);
                    userResponse.setRole(user.getRole().getRole());
                    return userResponse;
                })
                .toList();

        log.info("Fetched {} users (page {}/{})",
                userPage.getNumberOfElements(), pageNumber, userPage.getTotalPages());

        return PageResponse.<List<UserResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(userPage.getTotalPages())
                .numberOfElements(userPage.getNumberOfElements())
                .items(userResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllTeachers() {
        log.info("Fetching all teachers (no pagination)");
        List<User> teachers = userRepository.findAllTeachers();
        return teachers.stream().map(u -> {
            UserResponse dto = userMapper.toDTO(u);
            dto.setRole(u.getRole().getRole());
            return dto;
        }).toList();
    }

    @Override
    public UserResponse updateUser(Long id, MultipartFile avatar, AdminUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = findUserById(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            log.error("Cannot update user ID {}. Email '{}' already exists", id, request.getEmail());
            String message = messageSource.getMessage("user.email.exists.with.email",
                    new Object[] { request.getEmail() }, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        userMapper.updateEntityFromAdminUserDTO(request, user);
        Role role = roleService.findRoleByRoleName(request.getRole());
        user.setRole(role);
        user.setGender(Gender.getGenderFromName(request.getGender()));
        user.setStatus(Status.getStatusFromName(request.getStatus()));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.debug("Password updated for user ID: {}", id);
        }

        if (avatar != null && !avatar.isEmpty()) {
            log.info("Uploading new avatar for user ID: {}", id);
            String avatarUrl = storageService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
            log.info("Upload new avatar for user ID: {} successfully", id);
        }

        userRepository.save(user);
        log.info("User updated successfully with ID: {}", user.getId());
        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    public UserResponse updateProfile(Long id, UserRequest request) {
        log.info("Updating profile for user ID: {}", id);
        User user = findUserById(id);
        userMapper.updateEntityFromUserDTO(request, user);
        user.setGender(Gender.getGenderFromName(request.getGender()));
        userRepository.save(user);

        log.info("Profile updated successfully for user ID: {}", user.getId());
        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    public UserResponse updateAvatar(Long id, MultipartFile avatar) {
        log.info("Updating avatar for user ID: {}", id);
        User user = findUserById(id);
        if (avatar != null && !avatar.isEmpty()) {
            log.info("Uploading new avatar for user ID: {}", id);
            String avatarUrl = storageService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
            log.info("Upload new avatar for user ID: {} successfully", id);
        }
        userRepository.save(user);
        log.info("Avatar updated successfully for user ID: {}", id);
        UserResponse userResponse = userMapper.toDTO(user);
        userResponse.setRole(user.getRole().getRole());
        return userResponse;
    }

    @Override
    public Boolean isNoPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            String message = messageSource.getMessage("user.not.found.by.email", new Object[] { email },
                    LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(message);
        }
        return userRepository.getStatusPassword(email);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = findUserByEmail(request.getEmail());
        if (!user.getStatus().equals(Status.ACTIVE)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        mailService.sendResetLink(user);
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        User user = findUserById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void verifyEmail(String token) {
        String email = jwtService.extractEmail(token, TokenType.CONFIRM_TOKEN);
        User user = findUserByEmail(email);
        if (!user.getStatus().equals(Status.PENDING)
                || !jwtService.isTokenValid(token, user, TokenType.CONFIRM_TOKEN)) {
            String message = messageSource.getMessage("user.verifyEmail.failed", null, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void createPassword(UserCreationPasswordRequest request) {
        User user = findUserByEmail(request.getEmail());
        if (StringUtils.isBlank(user.getPassword()) && user.getNoPassword()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setNoPassword(false);
            userRepository.save(user);
        } else {
            String message = messageSource.getMessage("user.password.exists", null, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordRequest request) {
        String email = jwtService.extractEmail(token, TokenType.RESET_TOKEN);
        User user = findUserByEmail(email);
        if (!jwtService.isTokenValid(token, user, TokenType.RESET_TOKEN)
                || !whitelistTokenService.existsByToken(token)) {
            throw new InvalidTokenException();
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        whitelistTokenService.deleteByToken(token);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = findUserById(id);
        userRepository.delete(user);

        log.info("User deleted successfully with ID: {}", id);
    }

    public User findUserByEmail(String email) {
        log.info("Looking up user by email {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", email);
                    String message = messageSource.getMessage("user.not.found.by.email", new Object[] { email },
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

    private User findUserById(Long id) {
        log.debug("Looking up user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    String message = messageSource.getMessage("user.not.found.by.id", new Object[] { id },
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }
}
