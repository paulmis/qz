package server.database.entities.game.configuration;

import commons.entities.game.configuration.NormalGameConfigurationDTO;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

/**
 * Configuration for the normal game mode, which contains a fixed number of questions.
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class NormalGameConfiguration extends GameConfiguration {

    /**
     * Creates a new game configuration from a DTO.
     *
     * @param dto source DTO
     */
    public NormalGameConfiguration(NormalGameConfigurationDTO dto) {
        super(dto);
        this.numQuestions = dto.getNumQuestions();
    }

    /**
     * All arguments constructor for the normal game configuration.
     *
     * @param numQuestions number of questions in the game
     * @param answerTime time in seconds for answering a question
     */
    public NormalGameConfiguration(int numQuestions, int answerTime) {
        super(answerTime);
        this.numQuestions = numQuestions;
    }

    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    Integer numQuestions = 10;

    @Override
    public NormalGameConfigurationDTO getDTO() {
        return new ModelMapper().map(this, NormalGameConfigurationDTO.class);
    }
}
