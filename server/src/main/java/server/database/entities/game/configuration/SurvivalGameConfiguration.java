package server.database.entities.game.configuration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Configuration for the survival game mode.
 * In this mode, the player has to survive as long as possible, while the
 * time available to answer questions decreases with each round.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("survival")
public class SurvivalGameConfiguration extends GameConfiguration {
    /**
     * The speed increase/decrease of the game.
     */
    @Column(nullable = false)
    @NonNull Float speedModifier = 1.0f;
}
