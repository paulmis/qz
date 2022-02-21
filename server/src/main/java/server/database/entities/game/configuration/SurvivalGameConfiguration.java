package server.database.entities.game.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Entity
@MappedSuperclass
public class SurvivalGameConfiguration extends GameConfiguration {
    /**
     * The speed increase/decrease of the game.
     */
    @Column(nullable = false)
    @NonNull Float speedModifier = 1.0f;
}
