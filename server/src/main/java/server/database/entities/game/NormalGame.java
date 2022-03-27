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
import server.database.entities.game.configuration.NormalGameConfiguration;

/**
 * A game in the default game mode.
 */
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class NormalGame extends DefiniteGame<NormalGameDTO> {
    /**
     * Creates a new game from a DTO.
     *
     * @param dto source DTO
     */
    public NormalGame(NormalGameDTO dto) {
        super(dto);
    }

    /**
     * Returns the DTO of this game.
     *
     * @return the DTO of this game
     */
    @Override
    public NormalGameDTO getDTO() {
        return new NormalGameDTO(super.toDTO());
    }

    /**
     * Returns the configuration of this game.
     *
     * @return the configuration of this game
     */
    @Override
    public NormalGameConfiguration getConfiguration() {
        return (NormalGameConfiguration) super.getConfiguration();
    }

    public boolean isLastQuestion() {
        return this.currentQuestionNumber != null
            && this.currentQuestionNumber == getConfiguration().getNumQuestions() - 1;
    }

    /**
     * Returns whether the game should finish after this question is answered.
     *
     * @return whether the game should finish after this question is answered.
     */
    public boolean shouldFinish() {
        return isLastQuestion();
    }
}
