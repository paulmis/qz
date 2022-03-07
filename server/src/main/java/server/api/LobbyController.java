package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
import commons.entities.utils.DTO;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.User;
import server.database.entities.auth.config.AuthContext;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.utils.BaseEntity;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameConfigurationRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;

/*
made following https://spring.io/guides/tutorials/rest/
 */

/**
 * LobbyController, expose endpoints for lobbies.
 * Lobbies are games that haven't started yet.
 */
@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameConfigurationRepository gameConfigurationRepository;

    /**
     * Endpoint for the creation of new lobbies.
     *
     * @param gameDTO the DTO of the game to create.
     * @return 400 if the game constraints were violated, 404 if the user doesn't exist, 409 if the founder is
     *      already in a lobby or a game, 201 and the game otherwise
     */
    @PostMapping
    ResponseEntity<NormalGameDTO> create(@RequestBody NormalGameDTO gameDTO) {
        // If the user doesn't exist, return 404
        Optional<User> founder = userRepository.findByEmail(AuthContext.get());
        if (founder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check that the user isn't in another game
        if (gamePlayerRepository.existsByUserIdAndGameStatusNot(founder.get().getId(), GameStatus.FINISHED)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Create the game
        NormalGame game;
        try {
            game = new NormalGame(gameDTO);

            // Save the configuration
            NormalGameConfiguration config =
                    gameConfigurationRepository.save((NormalGameConfiguration) game.getConfiguration());
            game.setConfiguration(config);

            // Save the game
            game = gameRepository.save(game);
        } catch (ConstraintViolationException | PersistenceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Return 201
        return ResponseEntity.created(URI.create("/api/lobby/" + game.getId())).body(game.getDTO());
    }

    /**
     * Endpoint for available lobbies.
     *
     * @return a list of available lobbies.
     */
    @GetMapping("/available")
    ResponseEntity<List<GameDTO>> availableLobbies() {
        // It returns games with status Created
        List<GameDTO> lobbies = gameRepository
                .findAllByStatus(GameStatus.CREATED)
                .stream()
                .map(Game::getDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lobbies);
    }

    /**
     * Endpoint to get lobby info.
     *
     * @param lobbyId the UUID of the lobby.
     * @return information on the requested lobby.
     */
    @GetMapping("/{lobbyId}")
    ResponseEntity<DTO> get(@PathVariable @NonNull UUID lobbyId) {
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (!lobby.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(lobby.get().getDTO());
    }

    /**
     * Endpoint to allow a user to join a game.
     *
     * @param playerData information on the joining player (e.g. their nickname).
     * @param lobbyId    UUID of the lobby to join.
     * @param userId     UUID of the user that has to join.
     * @return true if the join was successful, false otherwise.
     */
    @PutMapping("/{lobbyId}/join/{userId}")
    ResponseEntity joinLobby(
            @RequestBody @NonNull GamePlayerDTO playerData,
            @PathVariable @NonNull UUID lobbyId,
            @PathVariable @NonNull UUID userId) {
        // ToDo: it should maybe check whether the user is allowed to join? Or is it the client's job?

        // Find lobby to join
        Optional<Game> toJoin = gameRepository.findById(lobbyId);
        if (!toJoin.isPresent()) {
            // No lobby
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (toJoin.get().getStatus() != GameStatus.CREATED) {
            // Game already started
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Find user trying to join
        Optional<User> joiningUser = userRepository.findById(userId);
        if (!joiningUser.isPresent()) {
            // No user
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Create player
        GamePlayer player = new GamePlayer(playerData, joiningUser.get());
        player.setUser(joiningUser.get());

        // Try to join the game
        if (toJoin.get().add(player)) {
            // Update repositories
            gameRepository.save(toJoin.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Endpoint to allow the lobby head to start the game.
     *
     * @param lobbyId the id of the lobby
     * @return 409 if the game has already been started or there aren't enough questions,
     *      404 if the lobby doesn't exist, 403 if the player isn't the lobby head, 200 otherwise
     */
    @PutMapping("/{lobbyId}/start")
    ResponseEntity start(@PathVariable @NonNull UUID lobbyId) {
        // If the user or the game don't exist, return 404
        Optional<User> founder = userRepository.findByEmail(AuthContext.get());
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (founder.isEmpty() || lobby.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // If the user isn't the lobby head, return 403
        if (lobby.get().getHead().getUser().getId() != founder.get().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // If the game doesn't start successfully, return 409
        try {
            gameService.startGame(lobby.get());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Otherwise, return 200
        return ResponseEntity.ok().build();
    }
}
