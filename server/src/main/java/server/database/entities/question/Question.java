package server.database.entities.question;

import commons.entities.AnswerDTO;
import commons.entities.questions.QuestionDTO;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.*;
import server.database.entities.utils.BaseEntity;
import server.services.answer.AnswerCollection;

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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "activities_asked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @ToString.Exclude
    @NonNull
    protected Set<Activity> activities = new HashSet<>();

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
    public abstract Map<UUID, Double> checkAnswer(AnswerCollection userAnswers) throws IllegalArgumentException;

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    public abstract AnswerDTO getRightAnswer();

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    protected QuestionDTO toDTO() {
        return new QuestionDTO(
                this.id,
                this.activities.stream().map(Activity::getDTO).collect(Collectors.toList()),
                this.text,
                null
        );
    }
}