package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import lombok.*;

/**
 * DTO for the reaction response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView(Views.Public.class)
public class ReactionDTO implements DTO {
    /**
     * The reaction type.
     */
    @NonNull
    protected String id;

}
