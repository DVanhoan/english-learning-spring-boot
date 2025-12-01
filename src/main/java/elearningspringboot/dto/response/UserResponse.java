package elearningspringboot.dto.response;

import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse extends BaseResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String address;
    private LocalDate birthDate;
    private UserRole role;
    private Status status;
    private Gender gender;
    private Boolean noPassword;
}
