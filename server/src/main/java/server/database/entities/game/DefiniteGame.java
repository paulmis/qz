package server.database.entities.game;

import commons.entities.game.DefiniteGameDTO;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import server.database.entities.game.configuration.NormalGameConfiguration;

/**
 * A game that has a defined amount of questions and is expected to end after the questions run out.
 */
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public abstract class DefiniteGame<T extends DefiniteGameDTO> extends Game<T> {
    protected int dummyField;

    /**
     * Creates a new game from a DTO.
     *
     * @param dto source DTO
     */
    public DefiniteGame(DefiniteGameDTO dto) {
        super(dto);
        this.dummyField = dto.dummyField;
    }

    /**
     * The amount of questions left in the game.
     *
     * @return The amount of questions left in the game.
     */
    public int getQuestionsCount() {
        return ((NormalGameConfiguration) getConfiguration()).getNumQuestions();
    }

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    protected DefiniteGameDTO toDTO() {
        return new DefiniteGameDTO(super.toDTO(), dummyField);
    }
}
