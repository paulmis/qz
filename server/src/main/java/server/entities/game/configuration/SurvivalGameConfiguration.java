package server.entities.game.configuration;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

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
    @NonNull Float speedModifier = 1.0f;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SurvivalGameConfiguration that = (SurvivalGameConfiguration) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
