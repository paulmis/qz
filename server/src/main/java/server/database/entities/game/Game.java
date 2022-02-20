package server.database.entities.game;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.question.Question;
import server.utils.EasyRandom;

/**
 * Game entity which represents a game and its state.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = javax.persistence.InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "game_mode")
public abstract class Game {
    @Id
    private Long id;

    /**
     * ID of the game shown to the user.
     * Should be randomly generated.
     */
    @Column(nullable = false, unique = true)
    private String gameId;

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
    @ToString.Exclude
    private Set<GamePlayer> players = Collections.synchronizedSet(new LinkedHashSet<>());


    /** Get the next question in the game.
     *
     * @return The current question.
     */
    public abstract Optional<Question> getNextQuestion();

    /** Add a player to the game.
     *
     * @param player The player to add.
     * @return Whether the player was added (and not already in the game).
     */
    public boolean addPlayer(GamePlayer player) {
        Objects.requireNonNull(player, "Player cannot be null.");
        return this.players.add(player);
    }

    /** Remove a player from the game.
     *
     * @param player The player to remove.
     * @return Whether the player was removed.
     */
    public boolean removePlayer(GamePlayer player) {
        Objects.requireNonNull(player, "Player cannot be null.");
        return this.players.remove(player);
    }

    /** Get the number of players in the game.
     *
     * @return The number of players.
     */
    public int getNumPlayers() {
        return players.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Game game = (Game) o;
        return id != null && Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
