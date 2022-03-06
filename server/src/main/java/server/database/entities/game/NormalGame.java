package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.game.NormalGameDTO;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A game in the default game mode.
 */
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class NormalGame extends DefiniteGame<NormalGameDTO> {
    public NormalGame(NormalGameDTO dto) {
        super(dto);
    }

    @Override
    public NormalGameDTO getDTO() {
        return new NormalGameDTO(super.toDTO());
    }
}
