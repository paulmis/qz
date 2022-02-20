package commons;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Answer class - describes an answer given by a player.
 */
@Data
@NoArgsConstructor
public class Answer {

    /**
     * The list of activities from the Question given as an answer.
     */
    public List<Activity> userChoice = new ArrayList<>();
}
