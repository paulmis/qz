package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
