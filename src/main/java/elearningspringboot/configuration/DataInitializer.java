package elearningspringboot.configuration;

import elearningspringboot.dto.request.AdminUserRequest;
import elearningspringboot.dto.request.RoleRequest;
import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import elearningspringboot.enumeration.UserRole;
import elearningspringboot.repository.RoleRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.RoleService;
import elearningspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAdminUser();
    }

    public void initAdminUser() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            AdminUserRequest admin = AdminUserRequest.builder()
                    .fullName("System Administrator")
                    .email("admin@example.com")
                    .phoneNumber("0912345678")
                    .password("Admin@123")
                    .address("System Address")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .role(UserRole.ADMIN.name())
                    .status(Status.ACTIVE.name())
                    .gender(Gender.MALE.name())
                    .build();
            userService.createUser(null, admin);
        }
    }

    public void initRoles() {
        if (!roleRepository.existsByRole(UserRole.USER)) {
            roleService.createRole(RoleRequest.builder()
                    .role("USER")
                    .description("Default system user with basic access rights")
                    .build());
        }
        if (!roleRepository.existsByRole(UserRole.ADMIN)) {
            roleService.createRole(RoleRequest.builder()
                    .role("ADMIN")
                    .description("System administrator with full access and management privileges")
                    .build());
        }
        if (!roleRepository.existsByRole(UserRole.TEACHER)) {
            roleService.createRole(RoleRequest.builder()
                    .role("TEACHER")
                    .description("Instructor role with privileges to manage courses and students")
                    .build());
        }
    }

}
