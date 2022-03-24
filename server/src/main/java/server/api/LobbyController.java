package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
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
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameConfigurationRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;
import server.services.GameService;
import server.services.LobbyService;
import server.services.SSEManager;


/**
 * LobbyController, expose endpoints for lobbies.
 * Lobbies are games that haven't started yet.
 */
@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    @Autowired
    private GameService gameService;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameConfigurationRepository gameConfigurationRepository;

    @Autowired
    private SSEManager sseManager;

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
            game.setGameId(RandomStringUtils.random(6, true, true));
            game.setStatus(GameStatus.CREATED);
            game.setAcceptingAnswers(false);

            // Save the configuration
            NormalGameConfiguration config =
                    gameConfigurationRepository.save((NormalGameConfiguration) game.getConfiguration());
            game.setConfiguration(config);

            // Create the player
            GamePlayer player = new GamePlayer(founder.get());
            game.add(player);

            // Save the game with the added host and player
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
    ResponseEntity<GameDTO> get(@PathVariable UUID lobbyId) {
        return gameRepository
                .findById(lobbyId)
                .map(game -> ResponseEntity.ok(game.getDTO()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to get lobby configuration info.
     *
     * @param lobbyId the UUID of the lobby.
     * @return information on the configuration of the requested lobby.
     */
    @GetMapping("/{lobbyId}/config")
    ResponseEntity<GameConfigurationDTO> lobbyConfigurationInfo(
            @PathVariable UUID lobbyId) {

        // Check if the lobby exists.
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        if (lobbyOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return ok status with configuration payload
        return ResponseEntity.ok(lobbyOptional.get().getConfiguration().getDTO());
    }

    /**
     * Endpoint to allow a user to join a game.
     *
     * @param lobbyId UUID of the lobby to join.
     * @return true if the join was successful, false otherwise.
     */
    @PutMapping("/{lobbyId}/join")
    ResponseEntity join(@PathVariable UUID lobbyId) {
        // If the user or the game doesn't exist, return 404
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        if (user.isEmpty() || lobbyOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Game lobby = lobbyOptional.get();

        // Create the player
        GamePlayer player = new GamePlayer(user.get());

        // Check that the game hasn't started yet and add the player
        if (lobby.getStatus() != GameStatus.CREATED
                || !lobby.add(player)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        lobby = gameRepository.save(lobby);
        return ResponseEntity.ok(lobby.getDTO());
    }

    /**
     * Endpoint to allow the lobby head to start the game.
     *
     * @param lobbyId the id of the lobby
     * @return 409 if the game has already been started or there aren't enough questions,
     *      404 if the lobby doesn't exist, 403 if the player isn't the lobby head, 200 otherwise
     */
    @PutMapping("/{lobbyId}/start")
    ResponseEntity start(@PathVariable UUID lobbyId) {
        // If the user or the game don't exist, return 404
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (user.isEmpty() || lobby.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // If the user isn't the lobby head, return 403
        if (lobby.get().getHost().getUser().getId() != user.get().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // If the game doesn't start successfully, return 409
        // If the SSE events are not yet set-up return 425
        try {
            gameService.startGame(lobby.get());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.TOO_EARLY).build();
        }
        // Otherwise, return 200
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to allow the host to change configuration.
     *
     * @param lobbyId               UUID of the lobby to join.
     * @param gameConfigurationData The new configuration data.
     * @return An ok status if successful.
     */
    @PostMapping("/{lobbyId}/config")
    ResponseEntity updateConfiguration(
            @PathVariable UUID lobbyId,
            @RequestBody GameConfigurationDTO gameConfigurationData) {
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        Optional<User> userOptional = userRepository.findByEmail(AuthContext.get());
        // Check if the lobby exists and user exists.
        if (lobbyOptional.isEmpty() || userOptional.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Game lobby = lobbyOptional.get();
        User user = userOptional.get();
        // Check if the lobby is created.
        if (lobby.getStatus() != GameStatus.CREATED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Check if the user is host.
        if (lobby.getHost().getUser().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Change and update lobby configuration based on configuration type.
        if (gameConfigurationData instanceof NormalGameConfigurationDTO) {
            lobby.setConfiguration(new NormalGameConfiguration((NormalGameConfigurationDTO) gameConfigurationData));
        } else {
            // Other configurations are not accepted as these are the only implemented game types as of now.
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        // Update repository.
        gameRepository.save(lobby);
        // Return an ok status.
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Endpoint to allow a player to leave the lobby.
     *
     * @return 404 if the player isn't in a lobby, 200 otherwise
     */
    @DeleteMapping("/leave")
    ResponseEntity leave() {
        // If the user or the game don't exist, return 404
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check that the user is in a lobby and remove them
        Optional<Game> lobby =
                gameRepository.findByPlayers_User_IdEqualsAndStatus(user.get().getId(), GameStatus.CREATED);
        if (lobby.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            lobbyService.removePlayer(lobby.get(), user.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a lobby. Only the host player can perform such action.
     *
     * @return 200 on success, 404 is player or lobby are not found, 401 if the player is not the lobby host
     */
    @DeleteMapping("/delete")
    ResponseEntity deleteLobby() {
        // Retrieve the logged-in user
        Optional<User> user = userRepository.findByEmail(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Find the user's lobby
        Optional<Game> lobby =
                gameRepository.findByPlayers_User_IdEqualsAndStatus(user.get().getId(), GameStatus.CREATED);
        if (lobby.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!lobbyService.deleteLobby(lobby.get(), user.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }
}
