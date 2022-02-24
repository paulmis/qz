package commons.entities;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Answer class - describes an answer given by a player.
 */
@Data
@NoArgsConstructor
@Generated
public class AnswerDTO {

    /**
     * The list of activities from the Question given as an answer.
     */
    protected List<ActivityDTO> userChoice = new ArrayList<>();
}
