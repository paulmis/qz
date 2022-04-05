package server.database.entities.question;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.AnswerDTO;
import commons.entities.questions.MCQuestionDTO;
import commons.entities.questions.QuestionDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import server.services.answer.AnswerCollection;

/**
 * MCQuestion data structure - describes a multiple choice question.
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class MCQuestion extends Question {

    /**
     * Activity corresponding to the correct answer.
     */
    @ManyToOne
    @JoinColumn(name = "answer_id")
    protected Activity answer;

    /**
     * Boolean to indicate if the user has to guess the energy consumption
     * or the corresponding activity.
     */
    protected boolean guessConsumption = true;

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public MCQuestion(MCQuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * Constructor for the MCQuestion class.
     *
     * @param id               the UUID of the question.
     * @param activities       the list of activities that compose the question.
     * @param text             the description of the question.
     * @param answer           the Activity that corresponds to the correct answer.
     * @param guessConsumption if the user has to guess the energy consumption of the activity
     *                         or the activity with a given consumption.
     */
    public MCQuestion(UUID id, Set<Activity> activities, String text, Activity answer, boolean guessConsumption) {
        super(activities, text);
        this.setId(id);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    /**
     * Copy constructor for the MCQuestion class.
     *
     * @param q                an instance of Question to copy.
     * @param answer           the Activity that corresponds to the correct answer.
     * @param guessConsumption if the user has to guess the energy consumption of the activity
     *                         or the activity with a given consumption.
     */
    public MCQuestion(Question q, Activity answer, boolean guessConsumption) {
        super(q);
        this.answer = answer;
        this.guessConsumption = guessConsumption;
    }

    /**
     * Constructor with only required fields.
     *
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     * @param answer     the Activity that corresponds to the correct answer.
     */
    public MCQuestion(Set<Activity> activities, String text, Activity answer) {
        super(activities, text);
        this.answer = answer;
    }

    /**
     * checkAnswer, checks if the answer of a multiple choice question is correct.
     *
     * @param userAnswers Answer collection of all users to check the answer for.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     *         mapped to GamePlayer ids.
     */
    @Override
    public Map<UUID, Double> checkAnswer(AnswerCollection userAnswers) throws IllegalArgumentException {
        log.trace("MCQuestion checkAnswer: checking answer for question {}", this.getId());

        // Verify that the answer is not null.
        if (userAnswers == null) {
            log.error("MCQuestion checkAnswer: userAnswers is null");
            throw new IllegalArgumentException("NULL input");
        }

        return userAnswers.getAnswers().stream().collect(
                Collectors.toMap(Map.Entry::getKey, answerEntry -> {
                    AnswerDTO answerDTO = answerEntry.getValue();
                    if (answer == null || answerDTO.getResponse().size() == 0) {
                        log.trace("User {} answered with no answer", answerEntry.getKey());
                        return 0.0;
                    }
                    // We don't want to throw here, as this allows rogue users to DoS the server.
                    if (answerDTO.getResponse().size() > 1) {
                        log.warn("User {} answered with more than one answer", answerEntry.getKey());
                        return 0.0;
                    }

                    if (answerDTO.getResponse().get(0).getCost() == answer.getCost()) {
                        log.trace("User {} answered correctly", answerEntry.getKey());
                        return 1.0;
                    } else {
                        log.trace("User {} answered incorrectly", answerEntry.getKey());
                        return 0.0;
                    }
                }));
    }

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    @Override
    public AnswerDTO getRightAnswer() {
        return new AnswerDTO(this.getId(), List.of(getAnswer().getDTO()));
    }

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    @Override
    public MCQuestionDTO getDTO() {
        UUID iconId = null;
        if (guessConsumption && answer != null && answer.getIconId() != null) {
            iconId = answer.getIconId();
        }
        QuestionDTO baseDTO = super.toDTO();
        baseDTO.setQuestionIconId(iconId);
        return new MCQuestionDTO(baseDTO, guessConsumption);
    }
}
