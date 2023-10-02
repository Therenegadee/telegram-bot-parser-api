package ru.telegramParser.user.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.telegramParser.user.model.enums.ERole;

@Entity
@Table(name = "roles")
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private ERole name;

}
