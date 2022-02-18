package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * Question data structure - describes a question of the quiz.
 */
/*
I followed this guide: https://tech.lalitbhatt.net/2014/07/mapping-inheritance-in-hibernate.html
to handle inheritance
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="QUESTION_TYPE", discriminatorType=DiscriminatorType.STRING)
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

    public Question(Question Q){
        this.activities = Q.activities;
        this.text = Q.text;
    }

    public Question(List<Activity> activities, String text) {
        this.activities = activities;
        this.text = text;
    }

    public abstract boolean CheckAnswer(List<Activity> userAnswer);

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