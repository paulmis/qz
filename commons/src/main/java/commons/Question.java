package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import lombok.Data;
import lombok.NoArgsConstructor;

/*
I followed this guide to handle inheritance:
https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
 */

/**
 * Question data structure - describes a question of the quiz.
 */
@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * List of activities used to generate the question.
     */
    @ManyToMany
    @JoinTable(
            name = "activities_asked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    public List<Activity> activities = new ArrayList<>();

    /**
     * Question asked the user.
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
     * @param userAnswers list of answers provided by each user.
     * @return a value between 0 and 1 indicating the percentage of points each user should get.
     */
    public abstract List<Double> checkAnswer(List<Answer> userAnswers);
}