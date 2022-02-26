package server.database.entities.question;

import commons.entities.AnswerDTO;
import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

/**
 * EstimateQuestion data structure - describes an estimate question.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Entity
public class EstimateQuestion extends Question {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public EstimateQuestion(QuestionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * checkAnswer, checks if the answer of an estimate question is correct.
     *
     * @param userAnswers list of answers provided by each user.
     *                    Each user should have a single activity as answer.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    @Override
    public List<Double> checkAnswer(List<AnswerDTO> userAnswers) throws IllegalArgumentException {
        if (userAnswers == null) {
            throw new IllegalArgumentException("NULL input");
        }

        List<Double> points = new ArrayList<>();
        // estimation error of each user
        List<Integer> errors = new ArrayList<>();
        // all the different errors, sorted and unique
        Set<Integer> sortedErrors = new TreeSet<>();

        // Get all estimation errors
        int target = activities.get(0).getCost();
        for (AnswerDTO ans : userAnswers) {
            if (ans.getUserChoice().size() != 1) {
                throw new IllegalArgumentException("There should be a single activity per answer.");
            }
            int userError = Math.abs(ans.getUserChoice().get(0).getCost() - target);
            errors.add(userError);
            sortedErrors.add(userError);
        }

        // For each user find their ranking
        double pointStep = 1.0 / (sortedErrors.size() - 1);
        for (int myError : errors) {
            double currentPoints = 1;
            for (int err : sortedErrors) {
                if (myError == err) {
                    break;
                }
                // the furthest a user is from the top rank the fewer points they get
                currentPoints -= pointStep;
            }
            if (currentPoints - pointStep < 0) {
                // This is to avoid rounding errors like 3*(1/3) != 1
                currentPoints = 0;
            }
            points.add(currentPoints);
        }

        return points;
    }
}
