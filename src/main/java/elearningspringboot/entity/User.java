package elearningspringboot.entity;

import elearningspringboot.enumeration.Gender;
import elearningspringboot.enumeration.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    private String phoneNumber;
    private String password;
    private String avatarUrl;
    private String address;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate birthDate;
    private Boolean noPassword;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", this.role.getRole().getName())));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.status.equals(Status.ACTIVE) || this.status.equals(Status.PENDING);
    }
}
