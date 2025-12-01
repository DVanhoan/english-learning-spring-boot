package elearningspringboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "{validation.password.not.blank}")
    @Size(min = 8, max = 160, message = "{validation.password.size}")
    private String newPassword;
}
