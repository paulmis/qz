package commons.entities;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
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
@JsonView(Views.Public.class)
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
