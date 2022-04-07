package server.configuration;

import commons.entities.questions.MCType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;
import server.utils.QuestionType;

/**
 * Configuration of the question generation process.
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "question")
public class QuestionGenerationConfiguration {
    /**
     * Number of attempts in generating a question from the activities before giving up.
     */
    private int questionGenerationAttempts = 1000;

    /**
     * Array of enabled question types.
     */
    private QuestionType[] enabledQuestionTypes = {
        new QuestionType(MCQuestion.class, MCType.GUESS_COST),
        new QuestionType(MCQuestion.class, MCType.GUESS_ACTIVITY),
        new QuestionType(MCQuestion.class, MCType.INSTEAD_OF),
        new QuestionType(EstimateQuestion.class),
    };

    /**
     * Returns the number of enabled question types.
     *
     * @return number of enabled question types.
     */
    public int getNumberEnabledQuestionTypes() {
        return enabledQuestionTypes.length;
    }
}
