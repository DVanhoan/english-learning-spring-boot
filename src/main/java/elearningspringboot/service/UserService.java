package elearningspringboot.service;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.UserResponse;
import elearningspringboot.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface UserService {

    UserResponse createUser(MultipartFile avatar, AdminUserRequest request);

    UserResponse registerUser(UserRequest request) throws MessagingException, UnsupportedEncodingException;

    UserResponse getUserById(Long id);

    User findUserByEmail(String email);

    PageResponse<List<UserResponse>> getUsersWithPaginationAndKeyword(int pageNumber, int pageSize, List<String> sorts,
            String keyword);

    List<UserResponse> getAllTeachers();

    UserResponse updateUser(Long id, MultipartFile avatar, AdminUserRequest request);

    UserResponse updateProfile(Long id, UserRequest request);

    UserResponse updateAvatar(Long id, MultipartFile avatar);

    Boolean isNoPassword(String email);

    void forgotPassword(ForgotPasswordRequest request) throws MessagingException, UnsupportedEncodingException;

    void updatePassword(Long id, UpdatePasswordRequest request);

    void resetPassword(String token, ResetPasswordRequest request);

    void verifyEmail(String token);

    void createPassword(UserCreationPasswordRequest request);

    void deleteUser(Long id);
}
