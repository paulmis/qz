package server.database.entities.question;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.questions.QuestionDTO;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

/**
 * MatchQuestion data structure - describes a match question.
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class MatchQuestion extends Question {

    /**
     * Constructor for the MatchQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     */
    public MatchQuestion(UUID id, List<Activity> activities, String text) {
        super(activities, text);
        this.setId(id);
    }

    /**
     * Copy constructor for the MatchQuestion class.
     *
     * @param mq an instance of Question to copy.
     */
    public MatchQuestion(Question mq) {
        super(mq);
    }

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public MatchQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * checkAnswer, checks if the answer of a match question is correct.
     *
     * @param userAnswers Answer collection of all users to check the answer for.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     *         mapped to GamePlayer ids.
     */
    @Override
    public Map<UUID, Double> checkAnswer(AnswerCollection userAnswers) throws IllegalArgumentException {
        log.trace("MatchQuestion checkAnswer: Checking answer for match question {}", this.getId());

        // Verify that the collection is not null
        if (userAnswers == null) {
            log.error("Attempting to check match question answers but collection is null");
            throw new IllegalArgumentException("NULL input");
        }

        final double pointStep = 1.0 / getActivities().size();

        return userAnswers.getAnswers().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        answerEntry -> {
                            if (answerEntry.getValue().getQuestionId() == null
                                    || !answerEntry.getValue().getQuestionId().equals(getId())) {
                                log.warn("MQ: checkAnswer: answer question ID does not match question ID (player {})",
                                        answerEntry.getKey());
                                return 0.0;
                            }

                            double currentPoints = 0;

                            for (ActivityDTO answer : answerEntry.getValue().getResponse()) {
                                // For mapping received answers to activities, we use original activity UUIDs
                                // These UUIDs are sent together with the question DTO, and can be used to
                                // retrieve the original activity from the question.
                                Activity originalActivity = getActivities().stream().filter(
                                        activity -> Objects.equals(activity.getId(), answer.getId())
                                ).findFirst().orElse(null);
                                // Check if we found a matching activity
                                if (originalActivity == null) {
                                    log.warn("MQ: checkAnswer: answer activity ID does not match"
                                            + " any question activity ID (player {})",
                                            answerEntry.getKey());
                                    return 0.0;
                                }

                                // Check if the answer is correct
                                if (originalActivity.getCost() == answer.getCost()) {
                                    currentPoints += pointStep;
                                }
                            }

                            if (currentPoints + pointStep > 1) {
                                // This is to avoid rounding errors like 3*(1/3) != 1
                                currentPoints = 1;
                            }

                            return currentPoints;
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
        AnswerDTO rightAnswer = new AnswerDTO();
        rightAnswer.setQuestionId(this.getId());
        rightAnswer.setResponse(getActivities().stream()
                .map(Activity::getDTO).collect(Collectors.toList()));
        return rightAnswer;
    }

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    @Override
    public QuestionDTO getDTO() {
        return new ModelMapper().map(this, QuestionDTO.class);
    }
}
