package server.database.entities.game;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Player entity, which represents an instance of a player in a specific game.
 * It has a many-to-one relationship to the User entity, and a one-to-one relationship to the Game entity.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GamePlayer {
    @Id private Long id;

    /**
     * Player's score.
     */
    private Integer score = 0;

    /**
     * The streak of correct answers in a row.
     */
    private Integer streak = 0;

    /**
     * The player's nickname within the game.
     */
    private String nickname;

    /**
     * The game the player is in.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    // TODO: add relation to the user entity.
}
