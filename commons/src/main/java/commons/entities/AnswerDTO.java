package commons.entities;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.time.LocalDateTime;
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
     * Time at which the answer was given.
     */
    protected LocalDateTime answerTime;

    /**
     * The list of activities from the Question given as an answer.
     */
    @NonNull protected List<ActivityDTO> response = new ArrayList<>();

    public AnswerDTO(UUID questionId, List<ActivityDTO> response) {
        this.questionId = questionId;
        this.response = response;
    }
}
