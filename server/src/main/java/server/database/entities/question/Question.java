package server.database.entities.question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import server.database.entities.game.Game;

/*
I followed this guide to handle inheritance:
https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
 */

/**
 * Question data structure - describes a question of the quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Generated
@Entity
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;

    /**
     * List of activities used to generate the question.
     */
    @ManyToMany
    @JoinTable(
            name = "activities_asked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @ToString.Exclude
    protected List<Activity> activities = new ArrayList<>();

    /**
     * List of games where this question has been asked.
     */
    @ManyToMany
    protected List<Game> games = new ArrayList<>();

    /**
     * Question asked the user.
     */
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
        this.games = q.games;
    }

    /**
     * checkAnswer, checks if the answer is correct.
     *
     * @param userAnswers list of answers provided by each user.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    public abstract List<Double> checkAnswer(List<Answer> userAnswers) throws IllegalArgumentException;
}