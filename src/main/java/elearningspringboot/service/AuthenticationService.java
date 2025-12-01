package elearningspringboot.service;

import elearningspringboot.dto.request.SignInRequest;
import elearningspringboot.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse signIn(SignInRequest request);

    TokenResponse authenticateGoogle(String code);

    TokenResponse refreshToken(String refreshToken);

    void signOut(String accessToken, String refreshToken);
}
