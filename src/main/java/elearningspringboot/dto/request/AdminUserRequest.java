package elearningspringboot.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.UserRole;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.ValueOfEnum;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserRequest {
        @NotBlank(message = "{validation.fullname.not.blank}")
        @Size(min = 3, max = 160, message = "{validation.fullname.size}")
        private String fullName;
        @NotBlank(message = "{validation.email.not.blank}")
        @Pattern(regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$", message = "{validation.email.invalid}")
        private String email;
        @Pattern(regexp = "^(?:\\+84|0)[35789][0-9]{8}$", message = "{validation.phone.invalid}")
        private String phoneNumber;
        @NotBlank(message = "{validation.password.not.blank}", groups = OnCreate.class)
        @Size(min = 8, max = 160, message = "{validation.password.size}")
        private String password;
        private String address;
        @NotNull(message = "{validation.birthdate.required}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate birthDate;
        @NotNull(message = "{validation.role.not.null}")
        @ValueOfEnum(enumClass = UserRole.class, message = "{validation.role.invalid}")
        private String role;
        @NotNull(message = "{validation.status.not.null}")
        @ValueOfEnum(enumClass = Status.class, message = "{validation.status.invalid}")
        private String status;
        @NotNull(message = "{validation.gender.not.null}")
        @ValueOfEnum(enumClass = Gender.class, message = "{validation.gender.invalid}")
        private String gender;
}
