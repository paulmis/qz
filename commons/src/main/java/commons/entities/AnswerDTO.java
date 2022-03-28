package commons.entities;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Answer class - describes an answer given by a player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.Public.class)
public class AnswerDTO implements DTO {
    /**
     * The list of activities from the Question given as an answer.
     */
    protected List<Long> response = new ArrayList<>();

    /**
     * The id of the question being answered.
     */
    protected UUID questionId;
}
