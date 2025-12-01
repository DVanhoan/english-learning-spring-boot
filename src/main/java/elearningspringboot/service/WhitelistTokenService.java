package elearningspringboot.service;

import elearningspringboot.enumeration.TokenType;

public interface WhitelistTokenService {
    void createToken(String token, TokenType tokenType, String email);

    void deleteByToken(String token);

    boolean existsByToken(String token);
}
