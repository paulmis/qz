package server.database.entities.game.configuration;

import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
    public NormalGameConfiguration(int numQuestions,
                                   Duration answerTime,
                                   int capacity,
                                   int streakSize,
                                   float multiplier,
                                   int pointsCorrect,
                                   int pointsWrong,
                                   int correctAnswerThreshold) {
        super(answerTime, capacity, streakSize, multiplier, pointsCorrect, pointsWrong, correctAnswerThreshold);
        this.numQuestions = numQuestions;
    }

    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    protected int numQuestions = 10;

    @Override
    public NormalGameConfigurationDTO getDTO() {
        var mapper = new ModelMapper();
        mapper.addConverter(
                context -> (int) context.getSource().toSeconds(),
                Duration.class, Integer.class);

        return mapper.map(this, NormalGameConfigurationDTO.class);
    }
}
