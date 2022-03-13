package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.utils.DTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameConfigurationRepository;
import server.database.repositories.game.GameRepository;

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
    private UserRepository userRepository;

    @Autowired
    private GameConfigurationRepository gameConfigurationRepository;

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
    ResponseEntity<DTO> lobbyInfo(@PathVariable @NonNull UUID lobbyId) {
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (!lobby.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(lobby.get().getDTO());
    }

    /**
     * Endpoint to get lobby configuration.
     *
     * @param lobbyId the UUID of the lobby.
     * @return information on the configuration of the requested lobby.
     */
    @GetMapping("/{lobbyId}/config")
    ResponseEntity<GameConfigurationDTO> lobbyConfiguration(
            @PathVariable @NonNull UUID lobbyId) {
        //Check if the lobby exists.
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (!lobby.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        //Check if the lobby has been created.
        if(lobby.get().getStatus() != GameStatus.CREATED) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(lobby.get().getDTO().getConfiguration());
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
     * Endpoint to allow the host to change configuration.
     *
     * @param lobbyId UUID of the lobby to join.
     * @param userId UUID of the user/host.
     * @return
     */
    @PostMapping("/{lobbyId}/config")
    ResponseEntity updateConfiguration(
            @PathVariable @NonNull UUID lobbyId,
            @PathVariable @NonNull UUID userId) {
        // Check if the lobby exists.
        Optional<Game> lobby = gameRepository.findById(lobbyId);
        if (!lobby.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        // Check if the lobby is created.
        if (lobby.get().getStatus() != GameStatus.CREATED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Check if the user exists
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Check if the user is in the lobby.
        if (!lobby.get().getPlayers().contains(user)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        // Check if the user is host.
        //ToDo: Method to check if the user is the host of the lobby.

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
