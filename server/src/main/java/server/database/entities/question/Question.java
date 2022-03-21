package server.database.entities.question;

import commons.entities.QuestionDTO;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import server.database.entities.answer.Answer;
import server.database.entities.utils.BaseEntity;

/**
 * Question data structure - describes a question of the quiz.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Question extends BaseEntity<QuestionDTO> {

    /**
     * List of activities used to generate the question.
     */
    @ManyToMany
    @JoinTable(
            name = "activities_asked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @ToString.Exclude
    @NonNull
    protected List<Activity> activities;

    /**
     * Question asked the user.
     */
    @NonNull
    protected String text;

    /**
     * Copy constructor for the Question class.
     *
     * @param q an instance of Question to copy.
     */
    public Question(Question q) {
        this.id = q.id;
        this.activities = q.activities;
        this.text = q.text;
    }

    /**
     * checkAnswer, checks if the answer is correct.
     *
     * @param userAnswers list of answers provided by each user.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    public abstract List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException;

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    public abstract Answer getRightAnswer();
}