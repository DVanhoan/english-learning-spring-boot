package elearningspringboot.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExchangeTokenResponse {
    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;
    private String refreshToken;
}