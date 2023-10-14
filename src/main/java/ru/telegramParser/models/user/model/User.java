package ru.telegramParser.models.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.telegramParser.models.user.model.enums.AuthState;
import ru.telegramParser.models.user.model.enums.UserState;

import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserState userState;

    @Enumerated(EnumType.STRING)
    private AuthState authState;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_parse_settings_id")
    private UserParseSetting userParseSetting;

    public User (String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
