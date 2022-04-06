package server.database.entities.game;

import java.util.UUID;
import javax.persistence.*;
import lombok.*;

/**
 * Entity for saved reactions.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    /**
     * Name of the reaction.
     */
    @Id private String name;

    /**
     * UUID of the reaction image (resource ID).
     */
    @Column(nullable = false)
    private UUID imageId;
}
