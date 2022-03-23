package server.database.entities.game.configuration;

import commons.entities.game.configuration.GameConfigurationDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import server.database.entities.utils.BaseEntity;

/**
 * Base class for game configuration.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class GameConfiguration extends BaseEntity<GameConfigurationDTO> {
    /**
     * Time (in seconds) available for each player to answer each question.
     * In the future, we could switch to a Duration datatype, but JPA/Hibernate doesn't support it out of the box.
     */
    @Column(nullable = false)
    protected int answerTime = 10;

    /**
     * Capacity of the lobby.
     */
    @Column(nullable = false)
    protected int capacity = 6;

    @Column(nullable = false)
    protected Integer streakSize = 3;

    @Column(nullable = false)
    protected Float streakMultiplier = 1.5f;

    @Column(nullable = false)
    protected Integer pointsCorrect = 100;

    @Column(nullable = false)
    protected Integer pointsWrong = 0;

    /**
     * Creates a new game configuration from a DTO.
     *
     * @param dto source DTO
     */
    public GameConfiguration(GameConfigurationDTO dto) {
        this.answerTime = dto.getAnswerTime();
        this.capacity = dto.getCapacity();
        this.streakSize = dto.getStreakSize();
        this.streakMultiplier = dto.getStreakMultiplier();
        this.pointsCorrect = dto.getPointsCorrect();
        this.pointsWrong = dto.getPointsWrong();
    }
}
