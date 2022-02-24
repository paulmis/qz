package server.database.entities.game;

import commons.entities.QuestionDTO;
import commons.entities.game.GamePlayerDTO;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import server.database.entities.User;
import server.database.entities.utils.BaseEntity;

/**
 * Player entity, which represents an instance of a player in a specific game.
 * It has a many-to-one relationship to the User entity, and a one-to-one relationship to the Game entity.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class GamePlayer extends BaseEntity<GamePlayerDTO> {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public GamePlayer(GamePlayerDTO dto) {
        ModelMapper mapper = new ModelMapper();
        mapper.map(dto, this);
    }

    /**
     * The unique identifier of the player.
     */
    @Id
    private UUID id;

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
    @NonNull private Game game;

    /**
     * The user the player is.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
