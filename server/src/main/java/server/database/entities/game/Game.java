package server.database.entities.game;

import java.util.*;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.question.Question;

/**
 * Game entity which represents a game and its state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Game {
    @Id
    private UUID id;

    /**
     * ID of the game shown to the user.
     * Should be randomly generated.
     */
    @Column(nullable = false, unique = true)
    private String gameId;

    /**
     * Timestmap of game creation.
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
     * List of players currently in the game.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<>());

    /**
     * List of questions assigned to this game.
     */
    @ManyToMany
    protected List<Question> questions = new ArrayList<>();

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
     * Adds questions to the game.
     *
     * @param questions The questions to add.
     */
    public void addQuestions(List<Question> questions) {
        this.questions.addAll(questions);
    }

    /**
     * Returns the current question. If no questions are available, returns empty optional.
     *
     * @return The current question or empty optional if none are available.
     */
    public Optional<Question> getCurrentQuestion() {
        try {
            return Optional.of(this.questions.get(this.currentQuestion));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
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
