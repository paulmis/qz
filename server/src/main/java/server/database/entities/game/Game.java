package server.database.entities.game;

import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import server.database.entities.game.configuration.GameConfiguration;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.game.exceptions.LastPlayerRemovedException;
import server.database.entities.question.Question;
import server.database.entities.utils.BaseEntity;
import server.utils.SaveableRandom;

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
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    protected GameConfiguration configuration;

    /**
     * Current status of the game.
     */
    protected GameStatus status = GameStatus.CREATED;

    /**
     * Current question number.
     */
    @Column(nullable = true)
    protected Integer currentQuestionNumber = null;

    /**
     * Seed used to generate the random numbers.
     */
    @JsonIgnore
    protected long seed;

    /**
     * Whether the players are allowed to submit an answer or not.
     */
    protected boolean acceptingAnswers = false;
    /**
     * List of players currently in the game mapped by their user IDs.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    protected Map<UUID, GamePlayer> players = new HashMap<>();
    /**
     * The head of the lobby - person in charge with special privileges.
     */
    @OneToOne(fetch = FetchType.LAZY)
    protected GamePlayer host;
    /**
     * Questions assigned to this game.
     */
    @ManyToMany
    protected List<Question> questions = new ArrayList<>();
    /**
     * PRNG.
     */
    @Transient
    private SaveableRandom random = new SaveableRandom(this.seed);
    
    /**
     * Creates a new game from a DTO.
     * Only an empty lobby (no players or questions) can be initialized.
     *
     * @param dto source DTO
     */
    public Game(GameDTO dto) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(
                context -> Duration.ofMillis(context.getSource()),
                Integer.class, Duration.class);
        mapper.getConfiguration().setSkipNullEnabled(true);
        if (dto.getId() == null) {
            // This id will change once the game entity is saved, but it must be non-null
            this.id = getUUID(0);
        } else {
            this.id = dto.getId();
        }
        this.gameId = dto.getGameId();
        this.createDate = dto.getCreateDate();
        if (dto.getConfiguration() instanceof NormalGameConfigurationDTO) {
            this.configuration = mapper.map(dto.getConfiguration(), NormalGameConfiguration.class);
        }
        this.status = dto.getStatus();
        this.currentQuestionNumber = dto.getCurrentQuestionNumber();
        this.gameType = dto.getGameType();
    }

    /**
     * Automatically sets the creation date to when the entity is first persisted.
     */
    @Generated
    @PrePersist
    void onCreate() {
        createDate = LocalDateTime.now();
    }

    /**
     * Returns ids of all users in the game.
     *
     * @return UUIDs of all GamePlayers in the current game.
     */
    public Set<UUID> getPlayerIds() {
        return players.values().stream().map(GamePlayer::getId).collect(Collectors.toSet());
    }

    /**
     * Get UUIDs of all players in the game.
     *
     * @return set of UUIDs of all players in the game.
     */
    public Set<UUID> getUserIds() {
        return players.keySet();
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
            return Optional.of(this.questions.get(this.currentQuestionNumber));
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return Optional.empty();
        }
    }

    /**
     * Increments the current question number and returns the question ID.
     */
    public Optional<UUID> incrementQuestion() {
        this.currentQuestionNumber = this.currentQuestionNumber == null
                ? 0
                : this.currentQuestionNumber + 1;
        return this.getQuestion().map(Question::getId);
    }

    /**
     * Determines whether this is the last question.
     *
     * @return whether this is the last question.
     */
    abstract boolean isLastQuestion();

    /**
     * Determines whether the game should finish.
     *
     * @return whether the game should finish.
     */
    public abstract boolean shouldFinish();

    /**
     * Computes the base score given a correctness percentage.
     * A base score means a score without streaks or multipliers applied.
     *
     * @param percentage the correctness percentage.
     * @return the computed base score.
     * @throws IllegalArgumentException if the percentage is not in the range [0, 1]
     */
    public int computeBaseScore(double percentage) throws IllegalArgumentException {
        if (percentage < 0.0 - 1e-9 || percentage > 1.0 + 1e-9) {
            throw new IllegalArgumentException("Percentage needs to be between 0 and 1.0.");
        }
        return (int) Math.round(percentage
                * (configuration.getPointsCorrect() - configuration.getPointsWrong())
                + configuration.getPointsWrong());
    }

    /**
     * Computes the streak score given a base score.
     *
     * @param gamePlayer the game player that has the streak.
     * @param baseScore  the base score.
     * @return the new streak score.
     */
    public int computeStreakScore(GamePlayer gamePlayer, double baseScore) {
        return (int) Math.round((gamePlayer.getStreak() >= configuration.getStreakSize())
                ? baseScore * configuration.getStreakMultiplier()
                : baseScore);
    }

    /**
     * Updates the streak of the game player based on if his answer was correct.
     * Resets the streak if an answer was wrong and adds 1 if the answer was right.
     *
     * @param gamePlayer the game player.
     * @param isCorrect  the boolean depicting if the answer was correct.
     */
    public void updateStreak(GamePlayer gamePlayer, boolean isCorrect) {
        gamePlayer.setStreak(isCorrect ? gamePlayer.getStreak() + 1 : 0);
    }

    /**
     * Get the number of players in the game.
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
     * Checks if the game is singleplayer, i.e. the capacity is 1.
     *
     * @return true if the game is singleplayer, false otherwise
     */
    public boolean isSingleplayer() {
        return getConfiguration().getCapacity() == 1;
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
                this.currentQuestionNumber,
                this.getQuestion().isPresent() ? this.getQuestion().get().getDTO() : null,
                this.players.values().stream().map(GamePlayer::getDTO).collect(Collectors.toSet()),
                this.host == null ? null : this.host.getId());
    }

    public abstract T getDTO();
}
