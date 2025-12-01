package elearningspringboot.controller;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.dto.response.TokenResponse;
import elearningspringboot.dto.response.UserResponse;
import elearningspringboot.service.AuthenticationService;
import elearningspringboot.service.UserService;
import elearningspringboot.util.ResponseBuilder;
import elearningspringboot.validation.OnCreate;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final MessageSource messageSource;

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseData<TokenResponse>> signIn(@Validated @RequestBody SignInRequest request) {
        TokenResponse response = authenticationService.signIn(request);
        String message = messageSource.getMessage("auth.sign.in.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<TokenResponse>> refreshToken(@RequestHeader("Y-Token") String refreshToken) {
        TokenResponse response = authenticationService.refreshToken(refreshToken);
        String message = messageSource.getMessage("auth.refresh.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ResponseData<Void>> signOut(@RequestHeader("X-Token") String accessToken,
            @RequestHeader("Y-Token") String refreshToken) {
        authenticationService.signOut(accessToken, refreshToken);
        String message = messageSource.getMessage("auth.logout.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseData<UserResponse>> registerUser(
            @RequestBody @Validated({ OnCreate.class, Default.class }) UserRequest request)
            throws MessagingException, UnsupportedEncodingException {
        log.info("Request: User register with data = {}", request);
        UserResponse response = userService.registerUser(request);
        log.info("Response: User registered = {}", response);
        String message = messageSource.getMessage("auth.register.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
    }

    @PostMapping("/authenticate/google")
    public ResponseEntity<ResponseData<TokenResponse>> authenticateGoogle(
            @NotBlank(message = "{validation.code.not.blank}") @RequestHeader("G-Code") String code) {
        TokenResponse response = authenticationService.authenticateGoogle(code);
        String message = messageSource.getMessage("auth.google.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping(value = "/check-no-password/{email}")
    public ResponseEntity<ResponseData<Boolean>> isNoPassword(@PathVariable("email") String email) {
        Boolean response = userService.isNoPassword(email);
        String message = messageSource.getMessage("user.noPassword.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping(value = "/create-password")
    public ResponseEntity<ResponseData<Void>> createPassword(@RequestBody UserCreationPasswordRequest request) {
        userService.createPassword(request);
        String message = messageSource.getMessage("user.password.create.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping(value = "/verify-email")
    public ResponseEntity<ResponseData<Void>> verifyEmail(@RequestHeader("C-Token") String confirmToken) {
        userService.verifyEmail(confirmToken);
        String message = messageSource.getMessage("user.verifyEmail.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<ResponseData<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request)
            throws MessagingException, UnsupportedEncodingException {
        userService.forgotPassword(request);
        String message = messageSource.getMessage("user.forgotPassword.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<Void>> validateResetToken(@RequestHeader("R-Token") String token,
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(token, request);
        String message = messageSource.getMessage("auth.resetPassword.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

}
