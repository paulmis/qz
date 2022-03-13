package server.database.entities.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.GameType;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import server.database.entities.Answer;
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
     * List of players currently in the game.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
    protected Set<GamePlayer> players = new HashSet<>();

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
     * Answers given by each player for each question.
     */
    @ManyToMany
    protected Map<Question, Map<GamePlayer, Answer>> answers = new HashMap<>();

    /**
     * Automatically sets the creation date to when the entity is first persisted.
     */
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
        this.id = dto.getId();
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
        if (isFull() || !this.players.add(player)) {
            return false;
        }
        player.setGame(this);

        // If the lobby is empty, add this player as the head of the lobby
        if (this.players.size() == 1) {
            this.host = player;
        }

        syncAnswers();
        return true;
    }

    /**
     * Remove a player from the game.
     *
     * @param playerId The id of the player to remove
     * @return Whether the player was removed.
     */
    public boolean remove(UUID playerId) throws LastPlayerRemovedException {
        // Remove the player from the game
        if (!this.players.removeIf(player -> player.getUser().getId().equals(playerId))) {
            return false;
        }

        // If the head left, replace them
        if (this.host.getUser().getId() == playerId) {
            this.host = this.players.stream()
                    .min(Comparator.comparing(GamePlayer::getJoinDate))
                    .orElse(null);

            // If the last player left, throw an exception
            if (this.host == null) {
                throw new LastPlayerRemovedException();
            }
        }

        syncAnswers();
        return true;
    }

    /**
     * Adds questions to the game.
     *
     * @param questions The questions to add.
     */
    public void addQuestions(List<Question> questions) {
        this.questions.addAll(questions);
        syncAnswers();
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
     * @param player the player giving the answer
     */
    public void addAnswer(Answer answer, GamePlayer player) {
        // Check if player is actually playing in this game
        if (!players.contains(player)) {
            return;
        }

        // Get current question
        Optional<Question> question = getQuestion();
        if (question.isEmpty()) {
            return;
        }

        // Get answers to current question
        Map<GamePlayer, Answer> currentAnswers = answers.get(question.get());
        if (currentAnswers == null) {
            return;
        }

        // Update player's answer
        currentAnswers.put(player, answer);
        answers.put(question.get(), currentAnswers);
    }

    /**
     * Returns the list of answers given by each player to the current question.
     *
     * @return answers given by each player to the current question
     */
    public List<Answer> getCurrentAnswers() {
        // Retrieve current question
        Optional<Question> question = getQuestion();
        if (question.isEmpty()) {
            return new ArrayList<>();
        }

        // Fill list
        List<Answer> currentAnswers = new ArrayList<>();
        for (GamePlayer player : players) {
            currentAnswers.add(answers.get(question).get(player));
        }
        return currentAnswers;
    }

    /**
     * Makes sure that every question has an answer from every player.
     */
    private void syncAnswers() {
        // Init
        Map<Question, Map<GamePlayer, Answer>> newAnswers = new HashMap<>();

        // Sync questions
        for (Question q : questions) {
            Map<GamePlayer, Answer> oldPlayerAnswers;
            if (answers.containsKey(q)) {
                oldPlayerAnswers = answers.get(q);
            } else {
                oldPlayerAnswers = new HashMap<>();
            }

            // Sync players
            Map<GamePlayer, Answer> newPlayerAnswers = new HashMap<>();
            for (GamePlayer p : players) {
                if (oldPlayerAnswers.containsKey(p)) {
                    newPlayerAnswers.put(p, oldPlayerAnswers.get(p));
                } else {
                    newPlayerAnswers.put(p, new Answer());
                }
            }

            newAnswers.put(q, newPlayerAnswers);
        }
        this.answers = newAnswers;
    }

    /**
     * Get the number of players in the game.
     *
     * @return the number of players
     */
    public int size() {
        return players.size();
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
                this.players.stream().map(GamePlayer::getDTO).collect(Collectors.toSet()),
                this.host == null ? null : this.host.getId());
    }

    public abstract T getDTO();
}
