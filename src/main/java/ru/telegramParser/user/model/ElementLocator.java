package ru.telegramParser.user.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.telegramParser.user.parseSettings.CssSelectorElement;
import ru.telegramParser.user.parseSettings.TagAttrElement;
import ru.telegramParser.user.parseSettings.XPathElement;
import ru.telegramParser.user.parseSettings.enums.ElementType;

import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Table(name = "ElementLocator")
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Inheritance(strategy = JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = XPathElement.class, name = "XPath"),
        @JsonSubTypes.Type(value = CssSelectorElement.class, name = "CSS Selector"),
        @JsonSubTypes.Type(value = TagAttrElement.class, name = "Tag+Attribute"),
})
public abstract class ElementLocator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ElementType type;
    private String pathToLocator;
    @ManyToOne
    private UserParseSetting userParseSetting;
}
