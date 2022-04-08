package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user reactions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.Public.class)
public class ReactionDTO {
    /**
     * ID of the user who reacted.
     */
    protected UUID userId;

    /**
     * Reaction type.
     */
    protected String reactionType;
}
