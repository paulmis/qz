package commons.game.configuration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for the survival game mode.
 * In this mode, the player has to survive as long as possible, while the
 * time available to answer questions decreases with each round.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("survival")
public class SurvivalGameConfiguration extends GameConfiguration {
    /**
     * The speed increase/decrease of the game.
     */
    @Column(nullable = false)
    private Float speedModifier = 1.0f;
}
