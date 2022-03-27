package server.database.entities.game.configuration;

import commons.entities.game.configuration.MockGameConfigurationDTO;
import java.time.Duration;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

/**
 * Mock game configuration entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class MockGameConfiguration extends GameConfiguration {

    /**
     * Creates a new game configuration from a DTO.
     *
     * @param dto source DTO
     */
    public MockGameConfiguration(MockGameConfigurationDTO dto) {
        super(dto);
    }

    /**
     * All arguments constructor for the normal game configuration.
     *
     * @param answerTime time in seconds for answering a question
     */
    public MockGameConfiguration(Duration answerTime,
                                   int capacity,
                                   int streakSize,
                                   float multiplier,
                                   int pointsCorrect,
                                   int pointsWrong,
                                   int correctAnswerThreshold) {
        super(answerTime, capacity, streakSize, multiplier, pointsCorrect, pointsWrong, correctAnswerThreshold);
    }

    @Override
    public MockGameConfigurationDTO getDTO() {
        return new ModelMapper().map(this, MockGameConfigurationDTO.class);
    }
}