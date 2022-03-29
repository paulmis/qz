package server.database.entities.question;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.questions.QuestionDTO;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
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
 * OrderQuestion data structure - describes a match question.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class OrderQuestion extends Question {

    /**
     * A boolean indicating whether the answer should be in increasing order.
     */
    protected boolean increasing = true;

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public OrderQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * Constructor for the OrderQuestion class.
     *
     * @param id         the UUID of the question.
     * @param activities the list of activities that compose the question.
     * @param text       the description of the question.
     * @param increasing if the user has to provide the answer in increasing order or not.
     */
    public OrderQuestion(UUID id, List<Activity> activities, String text, boolean increasing) {
        super(activities, text);
        this.setId(id);
        this.increasing = increasing;
    }

    /**
     * Copy constructor for the OrderQuestion class.
     *
     * @param q          an instance of Question to copy.
     * @param increasing if the user has to provide the answer in increasing order or not.
     */
    public OrderQuestion(Question q, boolean increasing) {
        super(q);
        this.increasing = increasing;
    }

    /**
     * checkAnswer, checks if the answer of an order question is correct.
     *
     * @param userAnswers list of answers provided by each user.
     *                    Each user should have a list activities as answer,
     *                    their order is checked to assign the points.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    @Override
    public Map<UUID, Double> checkAnswer(AnswerCollection userAnswers) throws IllegalArgumentException {
        if (userAnswers == null) {
            log.error("Attempting to check order question answers but collection is null");
            throw new IllegalArgumentException("NULL input");
        }

        final double pointStep = 1.0 / (getActivities().size() - 1);

        return userAnswers.getAnswers().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        answerEntry -> {
                            // Get the answer of the user
                            AnswerDTO answer = answerEntry.getValue();
                            // Verify that the number of activities in the answer
                            // matches the number of activities in the question
                            if (answer.getResponse().size() != getActivities().size()) {
                                // We don't want to throw here as this allows rogue users to DoS the server.
                                log.error("The number of activities in answer doesn't match the number of activities"
                                                + " in question for user {} (expected {}, got {})",
                                        answerEntry.getKey(),
                                        getActivities().size(),
                                        answer.getResponse().size());
                                return 0.0;
                            }

                            double currentPoints = 0;
                            // Create the function to score the answer
                            Function<List<ActivityDTO>, Double> comparator = values -> {
                                // Verify that both parameters are present
                                if (values.size() < 2 || values.get(0) == null || values.get(1) == null) {
                                    log.error("Attempting to check order question answers but one"
                                            + " of the activities is null or missing");
                                    return 0.0;
                                }

                                // Compare the first two activities
                                if (values.get(0).getCost() == values.get(1).getCost()) {
                                    return pointStep;
                                } else if (values.get(1).getCost() > values.get(0).getCost()) {
                                    return increasing ? pointStep : 0;
                                } else {
                                    return increasing ? 0 : pointStep;
                                }
                            };

                            // Score the answer using the constructed function
                            for (int idx = 1; idx < getActivities().size(); ++idx) {
                                currentPoints += comparator.apply(answer.getResponse().subList(idx - 1, idx + 1));
                            }

                            // Fix possible rounding errors
                            if (currentPoints + pointStep > 1) {
                                // This is to avoid rounding errors like 3*(1/3) != 1
                                currentPoints = 1;
                            }
                            return currentPoints;
                        }
                )
        );
    }

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    @Override
    public AnswerDTO getRightAnswer() {
        AnswerDTO rightAnswer = new AnswerDTO();
        rightAnswer.setResponse(getActivities().stream()
                .map(Activity::getDTO)
                .sorted((o1, o2) -> increasing
                        ? o1.getCost().compareTo(o2.getCost())
                        : o2.getCost().compareTo(o1.getCost()))
                .collect(Collectors.toList()));
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
