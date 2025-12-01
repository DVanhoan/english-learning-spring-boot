package elearningspringboot.service.impl;

import elearningspringboot.enumeration.TokenType;
import elearningspringboot.exception.InvalidTokenException;
import elearningspringboot.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.UUID;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.refresh-key}")
    private String REFRESH_KEY;

    @Value("${jwt.reset-key}")
    private String RESET_KEY;

    @Value("${jwt.confirm-key}")
    private String CONFIRM_KEY;

    @Value("${jwt.expiry-hour}")
    private long expiryHour;

    @Value("${jwt.expiry-day}")
    private long expiryDay;

    @Value("${jwt.expiry-minute}")
    private long expiryMinute;

    @Value("${spring.application.name}")
    private String provider;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
                .setIssuer(provider)
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuer(provider)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails, TokenType tokenType, long hour) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuer(provider)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * hour))
                .signWith(getKey(tokenType))
                .compact();
    }

    public Claims extractAllClaims(String token, TokenType tokenType) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(tokenType))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public String extractEmail(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getSubject();
    }

    public Date extractExpiration(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getExpiration();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String email = extractEmail(token, tokenType);
        if (!tokenType.equals(TokenType.CONFIRM_TOKEN))
            return userDetails.isEnabled() && email.equals(userDetails.getUsername())
                    && !isTokenExpired(extractExpiration(token, tokenType));
        else
            return email.equals(userDetails.getUsername()) && !isTokenExpired(extractExpiration(token, tokenType));

    }

    private boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date(System.currentTimeMillis()));
    }

    private Key getKey(TokenType tokenType) {
        byte[] keyBytes;
        keyBytes = switch (tokenType) {
            case ACCESS_TOKEN -> Decoders.BASE64.decode(SECRET_KEY);
            case REFRESH_TOKEN -> Decoders.BASE64.decode(REFRESH_KEY);
            case RESET_TOKEN -> Decoders.BASE64.decode(RESET_KEY);
            case CONFIRM_TOKEN -> Decoders.BASE64.decode(CONFIRM_KEY);
        };
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
