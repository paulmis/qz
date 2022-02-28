package server.database.entities.game;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.question.Question;
import server.database.entities.utils.BaseEntity;
import server.utils.EasyRandom;

/**
 * Game entity which represents a game and its state.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Game extends BaseEntity<GameDTO> {
    /**
     * ID of the game shown to the user.
     * Should be randomly generated.
     */
    @Column(nullable = false, unique = true)
    private String gameId;

    /**
     * Timestamp of game creation.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * Whether the game is public or not.
     */
    private GameType gameType = GameType.PUBLIC;

    /**
     * The game configuration.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private GameConfiguration configuration;

    /**
     * Current status of the game.
     */
    private GameStatus status = GameStatus.CREATED;

    /**
     * Current question number.
     */
    private Integer currentQuestion = 0;

    /**
     * State of the PRNG.
     */
    @NonNull @Embedded
    private EasyRandom random = new EasyRandom();

    /**
     * List of players currently in the game.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<>());


    /**
     * Get the next question in the game.
     *
     * @return The current question.
     */
    public abstract Optional<Question> getNextQuestion();

    /**
     * Add a player to the game.
     *
     * @param player The player to add.
     * @return Whether the player was added (and not already in the game).
     */
    public boolean add(GamePlayer player) {
        return this.players.add(player);
    }

    /**
     * Remove a player from the game.
     *
     * @param player The player to remove.
     * @return Whether the player was removed.
     */
    public boolean remove(GamePlayer player) {
        return this.players.remove(player);
    }

    /**
     * Get the number of players in the game.
     *
     * @return The number of players.
     */
    public int size() {
        return players.size();
    }
}
