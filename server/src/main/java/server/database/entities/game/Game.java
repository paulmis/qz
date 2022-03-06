package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.modelmapper.ModelMapper;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.question.Question;
import server.database.entities.utils.BaseEntity;
import server.utils.EasyRandom;

/**
 * Game entity which represents a game and its state.
 */
@EqualsAndHashCode(callSuper = true, exclude = {"random"})
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Game<T extends GameDTO> extends BaseEntity<T> {
    /**
     * ID of the game shown to the user.
     * Should be randomly generated.
     */
    // TODO: add a custom generation strategy
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
    private int currentQuestion = 0;

    /**
     * State of the PRNG.
     */
    @NonNull @Embedded @JsonIgnore
    private EasyRandom random = new EasyRandom();

    /**
     * List of players currently in the game.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<>());

    /**
     * Questions assigned to this game.
     */
    @ManyToMany
    private List<Question> questions = new ArrayList<>();

    /**
     * Creates a new game from a DTO. Doesn't copy players.
     *
     * @param dto source DTO
     */
    public Game(GameDTO dto) {
        ModelMapper mapper = new ModelMapper();
        this.id = dto.getId();
        this.gameId = dto.getGameId();
        this.createDate = dto.getCreateDate();
        if (dto.getConfiguration() instanceof NormalGameConfigurationDTO) {
            this.configuration = mapper.map(dto.getConfiguration(), NormalGameConfiguration.class);
        }
        this.status = dto.getStatus();
        this.currentQuestion = dto.getCurrentQuestion();
        this.gameType = dto.getGameType();
        this.players = new HashSet<>();
    }

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
    public Optional<Question> getQuestion() {
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

    /**
     * Converts the game superclass to a DTO.
     *
     * @return the game superclass DTO
     */
    protected GameDTO toDTO() {
        return new GameDTO(
                this.id,
                this.gameId,
                this.createDate,
                this.gameType,
                this.configuration.getDTO(),
                this.status,
                this.currentQuestion,
                this.players.stream().map(GamePlayer::getDTO).collect(Collectors.toSet()));
    }
}
