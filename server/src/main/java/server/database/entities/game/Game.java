package server.database.entities.game;

import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.Streams;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import server.database.entities.answer.Answer;
import server.database.entities.answer.AnswerCollection;
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
     * Answers given by each player for each question.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
    protected Map<Question, AnswerCollection> answers = new HashMap<>();

    /**
     * Automatically sets the creation date to when the entity is first persisted.
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
    protected boolean acceptingAnswers = false;

    /**
     * Toggle whether the players are allowed to submit an answer or not, and notify players about the change.
     *
     * @param acceptingAnswers whether the players are allowed to submit an answer or not.
     */
    public void changeAcceptingAnswers(boolean acceptingAnswers) throws IOException {
        this.acceptingAnswers = acceptingAnswers;
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
     * Sets the answer of a player to the current question.
     *
     * @param answer the answer to set
     * @param userId the id of the user giving the answer
     * @return true if the answer was correctly added
     */
    public boolean addAnswer(Answer answer, UUID userId) {
        // Check if player is actually playing in this game
        if (!players.containsKey(userId)) {
            return false;
        }

        // Fetch player
        GamePlayer player = players.get(userId);
        if (player == null) {
            return false;
        }

        // Set answer's player
        answer.setPlayer(player);

        // Get current question
        Optional<Question> question = getQuestion();
        if (question.isEmpty()) {
            return false;
        }

        // Get answers to current question
        AnswerCollection currentAnswers = answers.get(question.get());
        if (currentAnswers == null) {
            // Init tree if question is answered for the first time
            currentAnswers = new AnswerCollection();
            currentAnswers.setId(new AnswerCollection.Pk(getId(), question.get().getId()));
        }

        currentAnswers.addAnswer(answer);

        // Update answers to question
        answers.put(question.get(), currentAnswers);
        return true;
    }

    /**
     * Returns the list of answers given by each player to the current question.
     *
     * @return answers given by each player to the current question, sorted by player id
     */
    public List<Answer> getCurrentAnswers() {
        // Retrieve current question
        Optional<Question> questionOpt = getQuestion();
        if (questionOpt.isEmpty()) {
            return new ArrayList<>();
        }
        Question question = questionOpt.get();

        // Fill list and sort it by player id
        if (!answers.containsKey(question)) {
            // No answer given
            return new ArrayList<>();
        }
        List<Answer> currentAnswersList = answers.get(question).getAnswerList();
        return currentAnswersList;
    }

    /**
     * Updates the scores of the users based on the given question
     * and their answers.
     *
     * @param question the question that the answers are for.
     * @param answers the answers to the question.
     */
    public void updateScores(@NonNull Question question, @NonNull List<Answer> answers) {
        // Zips the game player with the base score of their answer(percentage of correctness).
        Map<GamePlayer, Double> scores =
                Streams.zip(
                        answers.stream().map(Answer::getPlayer),
                        question.checkAnswer(answers).stream(),
                        AbstractMap.SimpleEntry::new)
                        .collect(
                                Collectors.toMap(e -> e.getKey(),
                                e  -> computeBaseScore(e.getValue())
                                ));

        // Iterates over the players and updates their streak, computes their streak
        // score and sets their new score accordingly.
        players.values().forEach(gamePlayer -> {
            double score = Optional.ofNullable(scores.get(gamePlayer)).orElse(0.0);
            var isCorrect =  score >= configuration.getCorrectAnswerThreshold();

            updateStreak(gamePlayer, isCorrect);
            updatePowerUpPoints(gamePlayer, isCorrect);

            var streakScore = computeStreakScore(gamePlayer, score);
            gamePlayer.setScore(gamePlayer.getScore() + (int) Math.round(streakScore));
        });
    }

    /**
     * Computes the base score given a correctness percentage.
     * A base score means a score without streaks or multipliers applied.
     *
     * @param percentage the correctness percentage.
     * @return the computed base score.
     * @throws IllegalArgumentException if the percentage is not in the range [0, 1]
     */
    protected Double computeBaseScore(double percentage) throws IllegalArgumentException {
        if (percentage < 0.0 - 1e-9 || percentage > 1.0 + 1e-9) {
            throw new IllegalArgumentException("Percentage needs to be between 0 and 1.0.");
        }
        return percentage
                * (configuration.getPointsCorrect() - configuration.getPointsWrong())
                + configuration.getPointsWrong();
    }

    /**
     * Computes the streak score given a base score.
     *
     * @param gamePlayer the game player that has the streak.
     * @param baseScore the base score.
     * @return the new streak score.
     */
    protected Double computeStreakScore(GamePlayer gamePlayer, double baseScore) {
        return (gamePlayer.getStreak() >= configuration.getStreakSize())
                ? baseScore * configuration.getStreakMultiplier()
                : baseScore;
    }

    /**
     * Updates the streak of the game player based on if his answer was correct.
     * Resets the streak if an answer was wrong and adds 1 if the answer was right.
     *
     * @param gamePlayer the game player.
     * @param isCorrect the boolean depicting if the answer was correct.
     */
    protected void updateStreak(GamePlayer gamePlayer, boolean isCorrect) {
        gamePlayer.setStreak(isCorrect ? gamePlayer.getStreak() + 1 : 0);
    }

    /**
     * Updates the power-up points of the player based on if his answer is correct.
     *
     * @param gamePlayer the game player that added the answer.
     * @param isCorrect if the answer is correct.
     */
    protected void updatePowerUpPoints(GamePlayer gamePlayer, boolean isCorrect) {
        if (isCorrect) {
            gamePlayer.setPowerUpPoints(gamePlayer.getPowerUpPoints() + 1);
        }
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
