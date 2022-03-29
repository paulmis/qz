package server.configuration.quiz;

import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration management for quiz (game) related settings.
 */
@Data
@ConfigurationProperties(prefix = "quiz")
public class QuizConfiguration {
    /**
     * Game timing related configuration.
     */
    private QuizTimingConfiguration timing = new QuizTimingConfiguration();

    /**
     * How often to show the leaderboard.
     * (every X questions)
     */
    @Min(1)
    private int leaderboardInterval = 5;
}
