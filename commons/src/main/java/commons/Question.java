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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/*
I followed this guide: https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
to handle inheritance
 */
/**
 * Question data structure - describes a question of the quiz.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "QUESTION_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASE")
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToMany
    @JoinTable(
            name = "ActivitiesAsked",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    public List<Activity> activities;
    public String text;

    @SuppressWarnings("unused")
    protected Question() {
        // for object mapper
    }

    public Question(Question q) {
        this.activities = q.activities;
        this.text = q.text;
    }

    /**
     * Constructor for the Question class.
     *
     * @param activities list of activities that produce the question.
     * @param text text of the question.
     */
    public Question(List<Activity> activities, String text) {
        this.activities = activities;
        this.text = text;
    }

    /**
     * checkAnswer, checks if the answer is correct.
     *
     * @param userAnswer list of activities provided as answer.
     * @return true if the answer is correct, false otherwise.
     */
    public abstract boolean checkAnswer(List<Activity> userAnswer);

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}