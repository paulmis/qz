package server.database.entities.question;

import commons.entities.AnswerDTO;
import commons.entities.questions.EstimateQuestionDTO;
import commons.entities.questions.MCQuestionDTO;
import commons.entities.questions.QuestionDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import server.services.answer.AnswerCollection;
import server.utils.MathHelpers;

/**
 * EstimateQuestion data structure - describes an estimate question.
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class EstimateQuestion extends Question {

    /**
     * Constructor for the EstimateQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     */
    public EstimateQuestion(UUID id, Set<Activity> activities, String text) {
        super(activities, text);

        if (activities.size() != 1) {
            log.error("EstimateQuestion constructor: There should be a single activity per question.");
            throw new IllegalArgumentException("There should be a single activity per question.");
        }

        this.setId(id);
    }

    /**
     * Copy constructor for the EstimateQuestion class.
     *
     * @param mq an instance of Question to copy.
     */
    public EstimateQuestion(Question mq) {
        super(mq);
    }

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public EstimateQuestion(EstimateQuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * checkAnswer, checks if the answer of an estimate question is correct.
     *
     * @param userAnswers Answer collection of all users to check the answer for.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     *         mapped to GamePlayer ids.
     */
    @Override
    public Map<UUID, Double> checkAnswer(AnswerCollection userAnswers) throws IllegalArgumentException {
        log.trace("EstimateQuestion checkAnswer: Checking answer for question {}.", this.getId());

        // Verify that the collection is not null.
        if (userAnswers == null) {
            log.error("checkAnswer: userAnswers is null.");
            throw new IllegalArgumentException("Attempting to validate Estimate Question with null answers.");
        }

        // Create a map <GamePlayer UUID, Double> to store the results.
        return userAnswers.getAnswers().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    // Get the answer for the current user.
                    AnswerDTO answer = entry.getValue();

                    // Verify that we have exactly one activity as the answer.
                    // We don't want to throw here, as this allows rogue users to DoS the server.
                    if (answer.getResponse().size() != 1) {
                        log.warn("checkAnswer: userAnswers for user {} has more than one activity.",
                                entry.getKey());
                        return 0.0;
                    }

                    // Calculate the percentage of points for the user.
                    return MathHelpers.calculatePercentage(answer.getResponse().get(0).getCost(),
                            getActivities().iterator().next().getCost());
                }
        ));
    }

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    @Override
    public AnswerDTO getRightAnswer() {
        return new AnswerDTO(this.getId(), List.of(getActivities().iterator().next().getDTO()));
    }

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    @Override
    public EstimateQuestionDTO getDTO() {
        UUID iconId = null;
        if (activities.iterator().next() != null && activities.iterator().next().getIconId() != null) {
            iconId = activities.iterator().next().getIconId();
        }
        QuestionDTO baseDTO = super.toDTO();
        baseDTO.setQuestionIconId(iconId);
        return new EstimateQuestionDTO(baseDTO);
    }
}
