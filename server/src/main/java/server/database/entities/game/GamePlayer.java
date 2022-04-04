package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.PowerUp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
     * Maps power-up used to the question number that is was used on.
     * Allows for double point power-up to easily check.
     */
    @ElementCollection
    protected Map<PowerUp, Integer> userPowerUps = new HashMap<>();

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
     * @return the game player dto
     */
    public GamePlayerDTO getDTO() {
        var gamePlayerDTO = new ModelMapper().map(this, GamePlayerDTO.class);
        // Some info of the user are passed to the game DTO
        gamePlayerDTO.setNickname(user.getUsername());
        gamePlayerDTO.setProfilePic(user.getProfilePic());
        return gamePlayerDTO;
    }
}
