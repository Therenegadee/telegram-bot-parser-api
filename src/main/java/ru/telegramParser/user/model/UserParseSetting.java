package ru.telegramParser.user.model;


import jakarta.persistence.*;
import lombok.*;
import ru.telegramParser.user.parseSettings.enums.OutputFileType;

import java.util.List;

@Entity
@Table(name = "user_parse_settings")
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@AllArgsConstructor
@NoArgsConstructor
public class UserParseSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstPageUrl;
    private int numOfPagesToParse;
    private String className; // класс, содержащий в себе ссылкий на страницы
    private String tagName; // тэг, уточняющий класс
    private String cssSelectorNextPage; // CSS Selector кнопки переключения страниц
    private List<String> header;
    @OneToMany(mappedBy = "userParseSetting")
    private List<ElementLocator> parseSetting;
    @Enumerated(EnumType.STRING)
    private OutputFileType outputFileType;
    @OneToOne(mappedBy = "userParseSetting")
    private User user;
}
