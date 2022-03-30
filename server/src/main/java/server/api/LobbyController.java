package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.exceptions.GameAlreadyStartedException;
import server.api.exceptions.PlayerAlreadyInLobbyOrGameException;
import server.api.exceptions.SSEFailedException;
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
@Slf4j
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
    ResponseEntity create(@RequestBody NormalGameDTO gameDTO) throws SSEFailedException {
        // If the user doesn't exist, return 404
        Optional<User> founder = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (founder.isEmpty()) {
            log.debug("Founder doesn't exist");
            return ResponseEntity.notFound().build();
        }

        // Check that the user isn't in another game
        if (gamePlayerRepository.existsByUserIdAndGameStatusNot(founder.get().getId(), GameStatus.FINISHED)) {
            log.error("The user is already in a game");
            throw new IllegalStateException("User is already in a game");
        }

        NormalGame lobby = new NormalGame(gameDTO);
        lobby.setGameId(RandomStringUtils.random(6, true, true));
        lobby.setStatus(GameStatus.CREATED);
        lobby.add(new GamePlayer(founder.get()));

        // Save the game with the added host and player
        // If the game is singleplayer, start it
        if (lobby.isSingleplayer()) {
            lobby = (NormalGame) gameService.start(lobby);
        } else {
            lobby = gameRepository.save(lobby);
        }



        log.debug("Created a new game with id {}", lobby.getGameId());
        // Return 201
        return ResponseEntity
                .created(URI.create("/api/lobby/" + lobby.getId()))
                .body(lobby.getDTO());
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
     * Endpoint for player's current lobby/game.
     *
     * @return player's current lobby/game.
     */
    @GetMapping
    ResponseEntity<GameDTO> get() {
        // Get the user from the context
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the lobby/game
        Optional<Game> game = gameRepository.getPlayersGame(user.get().getId());
        if (game.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(game.get().getDTO());
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
    ResponseEntity lobbyConfigurationInfo(
            @PathVariable UUID lobbyId) {

        // Check if the lobby exists.
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        if (lobbyOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lobby not found");
        }
        // Return ok status with configuration payload
        return ResponseEntity.ok(lobbyOptional.get().getConfiguration().getDTO());
    }

    /**
     * Endpoint to allow a user to join a game.
     *
     * @param lobbyId UUID of the lobby to join.
     * @return 404 if the lobby doesn't exist, 409 if the player is already in the lobby or the lobby has started,
     *      200 and the lobby otherwise.
     */
    @PutMapping("/{lobbyId}/join")
    ResponseEntity join(@PathVariable UUID lobbyId) {
        // If the user or the game doesn't exist, return 404
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        if (user.isEmpty() || lobbyOptional.isEmpty()) {
            log.error("User or lobby not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or lobby not found");
        }
        Game<?> lobby = lobbyOptional.get();

        // Check that the player is not already in a lobby or a game
        if (gameRepository.getPlayersLobbyOrGame(user.get().getId()).isPresent()) {
            throw new PlayerAlreadyInLobbyOrGameException();
        }

        // Check that the game hasn't started yet and add the player
        GamePlayer player = new GamePlayer(user.get());
        if (lobby.getStatus() != GameStatus.CREATED || !lobby.add(player)) {
            throw new GameAlreadyStartedException(user.get(), lobby);
        }

        // Save the lobby
        lobby = gameRepository.save(lobby);
        log.debug("User {} joined game {}", user.get().getId(), lobby.getId());

        // Distribute the notifications to all players in the lobby
        try {
            sseManager.send(lobby.getUserIds(), new SSEMessage(SSEMessageType.LOBBY_MODIFIED));
        } catch (IOException ex) {
            log.error("Failed to notify players about the new player", ex);
        }

        // Return 200
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (user.isEmpty() || lobby.isEmpty()) {
            log.error("User or lobby not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or lobby not found");
        }

        // If the user isn't the lobby host, return 403
        if (lobby.get().getHost().getUser().getId() != user.get().getId()) {
            log.error("User is not the lobby host");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the lobby host");
        }

        // If the game doesn't start successfully, return 409
        // If the SSE events are not yet set-up return 425
        try {
            gameService.start(lobby.get());
        } catch (IOException ex) {
            log.error("Could not start game", ex);
            return ResponseEntity.status(HttpStatus.TOO_EARLY).body(ex.getMessage());
        }
        // Otherwise, return 200
        log.debug("Started game {}", lobby.get().getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to allow the host to change configuration.
     *
     * @param lobbyId               UUID of the lobby to join.
     * @param gameConfigurationData The new configuration data.
     * @return 400 if the provided configuration has an invalid type, 403 if the user isn't the lobby host,
     *      404 if the lobby doesn't exist, 409 if the game has already started, 200 otherwise
     */
    @PostMapping("/{lobbyId}/config")
    ResponseEntity updateConfiguration(
            @PathVariable UUID lobbyId,
            @RequestBody GameConfigurationDTO gameConfigurationData) {
        // Check if the lobby exists and user exists
        Optional<Game> lobbyOptional = gameRepository.findById(lobbyId);
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (lobbyOptional.isEmpty() || userOptional.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Game<?> lobby = lobbyOptional.get();
        User user = userOptional.get();

        // Check if the user is the lobby host
        if (lobby.getHost().getUser().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Check that the game hasn't started yet
        if (lobby.getStatus() != GameStatus.CREATED) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Change and update lobby configuration based on configuration type
        if (gameConfigurationData instanceof NormalGameConfigurationDTO) {
            lobby.setConfiguration(new NormalGameConfiguration((NormalGameConfigurationDTO) gameConfigurationData));
        } else {
            // Other configurations are not accepted as these are the only implemented game types as of now
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Update the repository
        gameRepository.save(lobby);
        log.info("Updated lobby {} configuration", lobby.getId());

        // Distribute the notifications to all players in the lobby
        try {
            sseManager.send(lobby.getUserIds(), new SSEMessage(SSEMessageType.LOBBY_MODIFIED));
        } catch (IOException ex) {
            log.error("Failed to notify players about the new configuration", ex);
        }

        // Return 200
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
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (userOptional.isEmpty()) {
            log.error("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();

        // Check that the user is in a lobby and remove them
        Optional<Game> lobbyOptional = gameRepository.getPlayersLobby(user.getId());
        if (lobbyOptional.isEmpty()) {
            log.debug("User {} is not in a lobby", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not in a lobby");
        }
        Game<?> lobby = lobbyOptional.get();

        // Remove the player and return 200
        lobbyService.removePlayer(lobby, user);
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
        Optional<User> user = userRepository.findByEmailIgnoreCase(AuthContext.get());
        if (user.isEmpty()) {
            log.error("User not found");
            return ResponseEntity.notFound().build();
        }

        // Find the user's lobby
        Optional<Game> lobby =
                gameRepository.getPlayersLobby(user.get().getId());
        if (lobby.isEmpty()) {
            log.debug("User {} is not in a lobby", user.get().getId());
            return ResponseEntity.notFound().build();
        }

        if (!lobbyService.deleteLobby(lobby.get(), user.get())) {
            log.debug("User {} is not the lobby host", user.get().getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Deleted lobby {}", lobby.get().getId());
        return ResponseEntity.ok().build();
    }
}
