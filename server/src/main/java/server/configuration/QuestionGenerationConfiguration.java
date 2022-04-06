package server.configuration;

import commons.entities.questions.MCType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;
import server.utils.QuestionType;

/**
 * Configuration of the question generation process.
 */
@Data
@ConfigurationProperties(prefix = "question")
public class QuestionGenerationConfiguration {
    /**
     * Number of attempts in generating a question from the activities before giving up.
     */
    public static final int questionGenerationAttempts = 1000;

    /**
     * Array of enabled question types.
     */
    public static final QuestionType[] enabledQuestionTypes = {
        new QuestionType(MCQuestion.class, MCType.GUESS_COST),
        new QuestionType(MCQuestion.class, MCType.GUESS_ACTIVITY),
        new QuestionType(MCQuestion.class, MCType.INSTEAD_OF),
        new QuestionType(EstimateQuestion.class),
    };
}
