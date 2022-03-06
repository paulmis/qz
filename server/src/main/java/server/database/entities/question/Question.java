package server.database.entities.question;

import commons.entities.AnswerDTO;
import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import server.database.entities.game.Game;
import server.database.entities.utils.BaseEntity;

/*
I followed this guide to handle inheritance:
https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
*/

/**
 * Question data structure - describes a question of the quiz.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
     * Games where this question is asked.
     */
    @ManyToMany
    protected List<Game> games = new ArrayList<>();

    /**
     * Copy constructor for the Question class.
     *
     * @param q an instance of Question to copy.
     */
    public Question(Question q) {
        this.id = q.id;
        this.activities = q.activities;
        this.text = q.text;
        this.games = q.games;
    }

    /**
     * checkAnswer, checks if the answer is correct.
     *
     * @param userAnswers list of answers provided by each user.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    public abstract List<Double> checkAnswer(List<AnswerDTO> userAnswers) throws IllegalArgumentException;

    /**
     * getRightAnswer, returns the correct answer for the question.
     *
     * @return the right answer
     */
    public abstract AnswerDTO getRightAnswer();
}