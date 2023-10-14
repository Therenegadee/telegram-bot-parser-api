package ru.telegramParser.models.user.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.telegramParser.models.user.model.enums.ERole;

@Entity
@Table(name = "roles",
        uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Role {

    public Role(ERole name) {
        this.name = name;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private ERole name;

}
