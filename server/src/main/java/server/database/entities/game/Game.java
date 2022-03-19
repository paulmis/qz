package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Question;
import server.database.entities.utils.BaseEntity;
import server.services.SSEManager;
import server.utils.SaveableRandom;

/**
 * Game entity which represents a game and its state.
 */
@EqualsAndHashCode(callSuper = true, exclude = {"random", "emitters"})
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
    protected String gameId;

    /**
     * Timestamp of game creation.
     */
    @Column(columnDefinition = "TIMESTAMP")
    protected LocalDateTime createDate;

    /**
     * Whether the game is public or not.
     */
    protected GameType gameType = GameType.PUBLIC;

    /**
     * The game configuration.
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected GameConfiguration configuration;

    /**
     * Current status of the game.
     */
    protected GameStatus status = GameStatus.CREATED;

    /**
     * Current question number.
     */
    protected int currentQuestion = 0;

    /**
     * Seed used to generate the random numbers.
     */
    @JsonIgnore
    protected long seed;

    /**
     * PRNG.
     */
    @Transient
    private SaveableRandom random = new SaveableRandom(this.seed);

    /**
     * List of players currently in the game mapped by their user IDs.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
    protected Map<UUID, GamePlayer> players = new HashMap<>();

    /**
     * The head of the lobby - person in charge with special privileges.
     */
    @OneToOne(fetch = FetchType.LAZY)
    protected GamePlayer host;

    /**
     * Mapping between game players and their corresponding SSE emitters.
     */
    @Transient
    @JsonIgnore
    public SSEManager emitters = new SSEManager();

    /**
     * Questions assigned to this game.
     */
    @ManyToMany
    protected List<Question> questions = new ArrayList<>();

    /**
     * Automatically sets the create date to when the entity is first persisted.
     */
    @Generated
    @PrePersist
    void onCreate() {
        createDate = LocalDateTime.now();
    }

    /**
     * Creates a new game from a DTO.
     * Only an empty lobby (no players or questions) can be initialized.
     *
     * @param dto source DTO
     */
    public Game(GameDTO dto) {
        ModelMapper mapper = new ModelMapper();
        if (dto.getId() == null) {
            // This id will change once the game entity is saved, but it must be non-null
            this.id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        } else {
            this.id = dto.getId();
        }
        this.gameId = dto.getGameId();
        this.createDate = dto.getCreateDate();
        if (dto.getConfiguration() instanceof NormalGameConfigurationDTO) {
            this.configuration = mapper.map(dto.getConfiguration(), NormalGameConfiguration.class);
        }
        this.status = dto.getStatus();
        this.currentQuestion = dto.getCurrentQuestion();
        this.gameType = dto.getGameType();
    }

    /**
     * Add a player to the game. Returns false if the lobby is already full or the player is already in this game.
     *
     * @param player the player to add
     * @return whether the player was added
     */
    public boolean add(GamePlayer player) {
        // Check if the player can be added
        if (isFull() || players.containsKey(player.getUser().getId())) {
            return false;
        }

        // Add the player
        this.players.put(player.getUser().getId(), player);
        player.setGame(this);

        // If the lobby is empty, add this player as the head of the lobby
        if (this.players.size() == 1) {
            this.host = player;
        }
        return true;
    }

    /**
     * Remove a player from the game. If the game is ongoing, the player will only be marked as abandoned.
     * If the game has concluded nothing will happen and the function will return false.
     *
     * @param userId the user id of the player to remove
     * @return whether the player was removed/marked as abandoned
     * @throws LastPlayerRemovedException if the last player left/abandoned the game
     */
    public boolean remove(UUID userId) throws LastPlayerRemovedException {
        switch (this.status) {
            case CREATED:
                // Remove the player from the game
                if (this.players.remove(userId) == null) {
                    return false;
                }

                // If the head left, replace them
                if (this.host.getUser().getId() == userId) {
                    this.host = this.players
                            .values().stream()
                            .min(Comparator.comparing(GamePlayer::getJoinDate))
                            .orElse(null);

                    // If the last player left, throw an exception
                    if (this.host == null) {
                        throw new LastPlayerRemovedException();
                    }
                }

                return true;

            case ONGOING:
                // If the player had already abandoned the game, return false
                if (!this.players.containsKey(userId) || this.players.get(userId).isAbandoned()) {
                    return false;
                }

                // Mark the player as abandoned
                this.players.get(userId).setAbandoned(true);

                // Check if all players abandoned the game
                if (size() == 0) {
                    throw new LastPlayerRemovedException();
                }

                return true;

            default:
                return false;
        }
    }

    /**
     * Whether the players are allowed to submit an answer or not.
     */
    @Transient
    protected boolean acceptingAnswers = false;

    /**
     * Toggle whether the players are allowed to submit an answer or not, and notify players about the change.
     *
     * @param acceptingAnswers whether the players are allowed to submit an answer or not.
     */
    public void setAcceptingAnswers(boolean acceptingAnswers) throws IOException {
        this.acceptingAnswers = acceptingAnswers;
        if (this.isAcceptingAnswers()) {
            this.emitters.sendAll(new SSEMessage(SSEMessageType.START_QUESTION));
        } else {
            this.emitters.sendAll(new SSEMessage(SSEMessageType.STOP_QUESTION));
        }
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
     * Get the number of players in the game. Does not count abandoned players.
     *
     * @return the number of players still in the game
     */
    public int size() {
        return (int) players.values().stream().filter(players -> !players.isAbandoned()).count();
    }

    /**
     * Checks if the lobby is full.
     *
     * @return whether the lobby is full
     */
    public boolean isFull() {
        return size() >= configuration.getCapacity();
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
                this.players.values().stream().map(GamePlayer::getDTO).collect(Collectors.toSet()),
                this.host == null ? null : this.host.getId());
    }

    public abstract T getDTO();
}
