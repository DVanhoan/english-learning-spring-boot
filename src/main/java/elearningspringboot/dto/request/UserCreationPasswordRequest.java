package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationPasswordRequest {
    @NotBlank(message = "{validation.email.not.blank}")
    @Pattern(regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$", message = "{validation.email.invalid}")
    private String email;
    @NotBlank(message = "{validation.password.not.blank}")
    @Size(min = 8, max = 160, message = "{validation.password.size}")
    private String password;
}
