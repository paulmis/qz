package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for DefiniteGame entity.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NormalGameDTO.class, name = "NormalGameDTO")
})
@JsonView(Views.Public.class)
public class DefiniteGameDTO extends GameDTO {
    /**
     * The amount of questions in the game.
     */
    public int dummyField;

    public DefiniteGameDTO(DefiniteGameDTO dto) {
        this(dto, dto.dummyField);
    }

    public DefiniteGameDTO(GameDTO superDTO, int dummyField) {
        super(superDTO);
        this.dummyField = dummyField;
    }
}
