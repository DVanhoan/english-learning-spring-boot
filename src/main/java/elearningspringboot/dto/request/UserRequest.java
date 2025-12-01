package elearningspringboot.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import elearningspringboot.enumeration.Gender;
import elearningspringboot.validation.OnCreate;
import elearningspringboot.validation.ValueOfEnum;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
        @NotBlank(message = "{validation.fullname.not.blank}")
        @Size(min = 3, max = 160, message = "{validation.fullname.size}")
        private String fullName;
        @NotBlank(message = "{validation.email.not.blank}", groups = OnCreate.class)
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
        @PastOrPresent(message = "{validation.birthdate.past.present}")
        private LocalDate birthDate;
        @NotNull(message = "{validation.gender.not.null}", groups = OnCreate.class)
        @ValueOfEnum(enumClass = Gender.class, message = "{validation.gender.invalid}")
        private String gender;
}
