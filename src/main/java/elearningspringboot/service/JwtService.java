package elearningspringboot.service;

import elearningspringboot.enumeration.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, TokenType tokenType, long hour);

    Claims extractAllClaims(String token, TokenType tokenType);

    String extractEmail(String token, TokenType tokenType);

    Date extractExpiration(String token, TokenType tokenType);

    boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);
}
