package commons.entities;

import commons.entities.utils.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Answer class - describes an answer given by a player.
 */
@Data
@NoArgsConstructor
public class AnswerDTO implements DTO {
    /**
     * The list of activities from the Question given as an answer.
     */
    protected List<? extends ActivityDTO> response = new ArrayList<>();

    /**
     * The id of the question being answered.
     */
    protected UUID questionId;
}
