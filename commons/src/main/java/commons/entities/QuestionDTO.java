package commons.entities;

import commons.entities.utils.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for question entities.
 */
@Data
@NoArgsConstructor
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
}
