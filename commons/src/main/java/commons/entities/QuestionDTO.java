package commons.entities;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for question entities.
 */
@Data
@NoArgsConstructor
@Generated
public class QuestionDTO implements Serializable {
    /**
     * UUID of the question.
     */
    private UUID id;

    /**
     * List of activities related to the question.
     */
    private List<ActivityDTO> activities;

    /**
     * Question text.
     */
    private String text;
}
