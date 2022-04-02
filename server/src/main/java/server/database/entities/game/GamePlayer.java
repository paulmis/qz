package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import commons.entities.game.GamePlayerDTO;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import server.database.entities.User;
import server.database.entities.utils.BaseEntity;

/**
 * Player entity, which represents an instance of a player in a specific game.
 * It has a many-to-one relationship to the User entity, and a one-to-one relationship to the Game entity.
 */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class GamePlayer extends BaseEntity<GamePlayerDTO> {

    /**
     * The user the player is.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @NonNull
    @EqualsAndHashCode.Include
    protected User user;

    /**
     * Indicates whether the player abandoned the game before it ended.
     */
    protected boolean abandoned = false;

    /**
     * Player's score.
     */
    protected Integer score = 0;

    /**
     * The streak of correct answers in a row.
     */
    protected Integer streak = 0;

    /**
     * The power-up points of a player which can be used to play power-ups.
     */
    protected Integer powerUpPoints = 0;

    /**
     * The date the player joined the lobby.
     */
    @Column(columnDefinition = "TIMESTAMP")
    protected LocalDateTime joinDate;

    /**
     * The game the player is in.
     */
    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude
    protected Game game;

    /**
     * Automatically sets the join date to when the entity is first persisted.
     */
    @PrePersist
    void onCreate() {
        joinDate = LocalDateTime.now();
    }


    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Creates a new game player from the DTO.
     *
     * @param dto the DTO to create the game player from.
     * @param user user the player is.
     */
    public GamePlayer(GamePlayerDTO dto, User user) {
        this.user = user;
        this.score = dto.getScore();
        this.streak = dto.getStreak();
    }

    /**
     * Converts to game player dto.
     *
     * @return the gameplayer dto
     */
    public GamePlayerDTO getDTO() {
        var gamePlayerDTO = new ModelMapper().map(this, GamePlayerDTO.class);
        gamePlayerDTO.setNickname(getUsername());
        return gamePlayerDTO;
    }
}
