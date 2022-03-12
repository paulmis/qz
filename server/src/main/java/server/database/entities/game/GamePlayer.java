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
@EqualsAndHashCode(callSuper = true)
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
    protected User user;

    /**
     * The player's nickname within the game.
     */
    @Column
    protected String nickname;

    /**
     * Player's score.
     */
    @Column
    protected Integer score = 0;

    /**
     * The streak of correct answers in a row.
     */
    @Column
    protected Integer streak = 0;

    /**
     * The date the player joined the lobby.
     */
    @Column
    protected LocalDateTime joinDate;

    /**
     * The game the player is in.
     */
    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @EqualsAndHashCode.Exclude
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
     * Returns player's nickname. If one isn't set, returns their username.
     *
     * @return player's nickname
     */
    public String getNickname() {
        return nickname == null && user != null ? user.getUsername() : nickname;
    }

    /**
     * Creates a new game player from the DTO.
     *
     * @param dto the DTO to create the game player from.
     * @param user user the player is.
     */
    public GamePlayer(GamePlayerDTO dto, User user) {
        this.user = user;
        this.nickname = dto.getNickname();
        this.score = dto.getScore();
        this.streak = dto.getStreak();
    }

    public GamePlayerDTO getDTO() {
        return new ModelMapper().map(this, GamePlayerDTO.class);
    }
}
