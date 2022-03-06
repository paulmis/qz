package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for NormalGame entity.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class NormalGameDTO extends DefiniteGameDTO {
    public NormalGameDTO(DefiniteGameDTO superDTO) {
        super(superDTO);
    }
}
