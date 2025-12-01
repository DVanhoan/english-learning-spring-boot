package elearningspringboot.controller;

import elearningspringboot.dto.request.AdminUserRequest;
import elearningspringboot.dto.request.UpdatePasswordRequest;
import elearningspringboot.dto.request.UserRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.dto.response.UserResponse;
import elearningspringboot.service.UserService;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.OnUpdate;
import elearningspringboot.validation.ValidImageFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static elearningspringboot.util.AppUtils.getUserIdFromSecurityContext;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ResponseData<PageResponse<List<UserResponse>>>> getAllUsersWithPagination(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword) {
        log.info("Request: Get users with pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber, pageSize,
                sorts, keyword);
        PageResponse<List<UserResponse>> response = userService.getUsersWithPaginationAndKeyword(pageNumber, pageSize,
                sorts, keyword);
        log.info("Response: {} users fetched (page {}/{})", response.getNumberOfElements(), response.getPageNumber(),
                response.getTotalPages());
        String message = messageSource.getMessage("user.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getAllTeachers() {
        log.info("Request: Get all teachers (no pagination)");
        List<UserResponse> response = userService.getAllTeachers();
        String message = messageSource.getMessage("user.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<UserResponse>> getUserById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get user by ID = {}", id);
        UserResponse response = userService.getUserById(id);
        log.info("Response: Found user = {}", response);
        String message = messageSource.getMessage("user.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData<UserResponse>> createUser(
            @ValidImageFile(required = false, message = "{validation.image.file.invalid}") @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart("user") @Validated({ OnCreate.class, Default.class }) AdminUserRequest request) {
        log.info("Request: Admin create user with data = {}", request);
        UserResponse response = userService.createUser(avatar, request);
        log.info("Response: User created = {}", response);
        String message = messageSource.getMessage("user.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData<UserResponse>> updateUser(
            @PathVariable("id") @Min(value = 1, message = "Id must be greater than 0") Long id,
            @ValidImageFile(required = false, message = "{validation.image.file.invalid}") @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart("user") @Validated({ OnUpdate.class, Default.class }) AdminUserRequest request) {
        log.info("Request: Update user with ID = {}, data = {}", id, request);
        UserResponse response = userService.updateUser(id, avatar, request);
        log.info("Response: User updated = {}", response);
        String message = messageSource.getMessage("user.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> deleteUser(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete user with ID = {}", id);
        userService.deleteUser(id);
        log.info("Response: User deleted with ID = {}", id);
        String message = messageSource.getMessage("user.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseData<UserResponse>> getProfile() {
        Long userId = getUserIdFromSecurityContext();
        UserResponse response = userService.getUserById(userId);
        String message = messageSource.getMessage("user.profile.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PutMapping("/me")
    public ResponseEntity<ResponseData<UserResponse>> updateProfile(
            @RequestBody @Validated({ OnUpdate.class, Default.class }) UserRequest request) {
        Long userId = getUserIdFromSecurityContext();

        log.info("Request: Update profile for user ID = {}, data = {}", userId, request);
        UserResponse response = userService.updateProfile(userId, request);
        log.info("Response: Profile updated = {}", response);

        String message = messageSource.getMessage("user.profile.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ResponseData<Void>> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        Long userId = getUserIdFromSecurityContext();
        log.info("Request: Update password for user ID = {}", userId);
        userService.updatePassword(userId, request);
        log.info("Response: password updated");
        String message = messageSource.getMessage("user.password.update.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData<UserResponse>> updateAvatar(
            @ValidImageFile @RequestParam("avatar") MultipartFile avatar) {
        Long userId = getUserIdFromSecurityContext();
        log.info("Request: Update avatar for user ID = {}", userId);
        UserResponse response = userService.updateAvatar(userId, avatar);
        log.info("Response: Avatar updated");

        String message = messageSource.getMessage("user.avatar.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

}
