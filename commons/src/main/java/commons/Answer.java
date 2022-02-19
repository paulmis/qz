package commons;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Answer class - describes an answer given by a player.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Answer {

    /**
     * The list of activities from the Question given as an answer.
     */
    public List<Activity> userChoice;
}
