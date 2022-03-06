package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import commons.entities.game.NormalGameDTO;
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
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.utils.BaseEntity;
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
    ResponseEntity<List<DTO>> availableLobbies() {
        // ToDo: it should respond only to authenticated users
        // It returns games with status Created
        List<DTO> lobbies = gameRepository
                .findAllByStatus(GameStatus.CREATED)
                .stream()
                .map(BaseEntity::getDTO)
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
        // ToDo: it should respond only to authenticated users
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
        // ToDo: it should respond only to authenticated users
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
}
