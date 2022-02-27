package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GamePlayerRepository;
import server.database.repositories.game.GameRepository;

/*
made following https://spring.io/guides/tutorials/rest/
 */

/**
 * LobbyController, expose endpoints for lobbies.
 * Lobbies are games that haven't started yet.
 */
@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    /**
     * Repository of the games.
     */
    @Autowired private GameRepository gameRepository;

    /**
     * Repository of the users.
     */
    @Autowired private UserRepository userRepository;

    /**
     * Repository of the players.
     */
    @Autowired private GamePlayerRepository gamePlayerRepository;

    /**
     * Endpoint for available lobbies.
     *
     * @return a list of available lobbies.
     */
    @GetMapping(path = {"", "/available"})
    ResponseEntity<List<GameDTO>> availableLobbies() {
        // ToDo: it should respond only to authenticated users
        // It returns games with status Created
        List<GameDTO> lobbies = gameRepository.findAllByStatus(GameStatus.CREATED)
                .stream().map(g -> g.getDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(lobbies, HttpStatus.OK);
    }

    /**
     * Endpoint to get lobby info.
     *
     * @param lobbyId the UUID of the lobby.
     * @return information on the requested lobby.
     */
    @GetMapping(path = {"", "/{lobbyId}"})
    ResponseEntity<GameDTO> lobbyInfo(@PathVariable @NonNull UUID lobbyId) {
        // ToDo: it should respond only to authenticated users
        Game lobby = gameRepository.getById(lobbyId);
        if (lobby == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lobby.getDTO(), HttpStatus.OK);
    }

    /**
     * Endpoint to allow a user to join a game.
     *
     * @param playerData information on the joining player (e.g. their nickname).
     * @param lobbyId    UUID of the lobby to join.
     * @param userId     UUID of the user that has to join.
     * @return true if the join was successful, false otherwise.
     */
    @PutMapping(path = {"", "/{lobbyId}/join/{userId}"})
    ResponseEntity<Boolean> joinLobby(
            @RequestBody @NonNull GamePlayerDTO playerData,
            @PathVariable @NonNull UUID lobbyId,
            @PathVariable @NonNull UUID userId) {
        // ToDo: it should respond only to authenticated users
        // ToDo: it should maybe check whether the user is allowed to join? Or is it the client's job?

        // Find lobby to join
        Game toJoin = gameRepository.getById(lobbyId);
        if (toJoin == null) {
            // No lobby.
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if (toJoin.getStatus() != GameStatus.CREATED) {
            // Game already started.
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

        // Find user trying to join
        User joiningUser = userRepository.getById(userId);
        if (joiningUser == null) {
            // No user.
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        // Create player
        GamePlayer player = new GamePlayer(playerData);
        player.setUser(joiningUser);

        // Try to join the game
        boolean success = toJoin.add(player);
        if (success) {
            // Update repositories
            gameRepository.save(toJoin);
            gamePlayerRepository.save(player);
            userRepository.save(joiningUser);
        }
        return new ResponseEntity<>(success, success ? HttpStatus.OK : HttpStatus.CONFLICT);
    }
}
