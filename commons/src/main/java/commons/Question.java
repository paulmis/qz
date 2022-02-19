package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/*
I followed this guide to handle onheritance:
https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
 */

/**
 * Question data structure - describes a question of the quiz.
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "QUESTION_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASE")
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    /**
     * List of activities used to generate the question.
     */
    @ManyToMany
    @JoinTable(
            name = "ActivitiesAsked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    public List<Activity> activities;
    /**
     * String of the question asked the user.
     */
    public String text;

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
     * @param userAnswer list of activities provided as answer.
     * @return true if the answer is correct, false otherwise.
     */
    public abstract boolean checkAnswer(List<Activity> userAnswer);
}