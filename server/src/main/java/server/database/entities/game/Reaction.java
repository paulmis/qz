package server.database.entities.game;

import java.util.UUID;
import javax.persistence.*;
import lombok.*;

/**
 * Entity for saved reactions.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reaction", indexes = {
        @Index(name = "idx_reaction_name_unq", columnList = "name", unique = true)
})
public class Reaction {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private UUID id;

    /**
     * Name of the reaction.
     */
    @Column(nullable = false, unique = true)
    @NonNull private String name;

    /**
     * UUID of the reaction image (resource ID).
     */
    @Column(nullable = false)
    @NonNull private UUID imageId;
}
