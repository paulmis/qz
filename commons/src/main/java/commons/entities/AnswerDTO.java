package commons.entities;

import commons.entities.utils.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;


/**
 * Answer class - describes an answer given by a player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO implements DTO {
    /**
     * The id of the question being answered.
     */
    protected UUID questionId;

    /**
     * The list of activities from the Question given as an answer.
     */
    @NonNull protected List<ActivityDTO> response = new ArrayList<>();
}
