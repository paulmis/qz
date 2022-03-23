package commons.entities.questions;

import commons.entities.ActivityDTO;
import commons.entities.utils.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for Questions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class QuestionDTO implements DTO {
    /**
     * UUID of the question.
     */
    protected UUID id;

    /**
     * List of activities related to the question.
     */
    protected List<ActivityDTO> activities = new ArrayList<>();

    /**
     * Question text.
     */
    protected String text;

    /**
     * Copy constructor.
     *
     * @param questionDTO the question DTO to copy
     */
    public QuestionDTO(QuestionDTO questionDTO) {
        this.id = questionDTO.id;
        this.activities = questionDTO.activities;
        this.text = questionDTO.text;
    }
}
