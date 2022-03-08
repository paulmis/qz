package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.game.GameDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for DefiniteGame entity.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
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
