package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.game.Game;
import server.database.entities.game.GamePlayer;
import server.database.repositories.game.GameRepository;

/*
made following https://spring.io/guides/tutorials/rest/
 */

/**
 * LobbyController, expose endpoints for lobbies.
 */
@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    /**
     * Repository of the lobbies.
     */
    @Autowired
    private GameRepository lobbyRepository;

    /**
     * Endpoint for testing. To be removed.
     *
     * @return a list of available lobbies.
     */
    @GetMapping(path = {"", "/test/{param}"})
    ResponseEntity<List<GameDTO>> testEndpoint(
            @RequestParam(required = false) Integer id, @PathVariable(required = false) Optional<String> param) {
        if (!param.isPresent()) {
            System.out.println(id);
        } else {
            System.out.println(param.get() + ", " + id);
        }
        List<GameDTO> lobbies = lobbyRepository.findAllByStatus(GameStatus.CREATED)
                .stream().map(g -> g.getDTO()).collect(Collectors.toList());
        if (id == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if (id.equals(42)) {
            return new ResponseEntity<>(null, HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<>(lobbies, HttpStatus.OK);
    }

    /**
     * Endpoint for available lobbies.
     *
     * @return a list of available lobbies.
     */
    @GetMapping(path = {"", "/available"})
    ResponseEntity<List<GameDTO>> availableLobbies() {
        // ToDo: it should respond only to authenticated users
        // It returns games with status Created
        List<GameDTO> lobbies = lobbyRepository.findAllByStatus(GameStatus.CREATED)
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
        Game lobby = lobbyRepository.getById(lobbyId);
        if (lobby == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lobby.getDTO(), HttpStatus.OK);
    }

    /**
     * Endpoint to allow a user to join a game.
     *
     * @param playerData information on the player.
     * @param gameId     UUID of the game to join.
     * @return true if the join was successful, false otherwise.
     */
    @PostMapping(path = {"", "/join/{gameId}"})
    ResponseEntity<Boolean> joinLobby(
            @RequestBody @NonNull GamePlayerDTO playerData, @PathVariable @NonNull UUID gameId) {
        // ToDo: it should respond only to authenticated users
        // ToDo: should maybe check whether the user is allowed to join? Or it's the client's job?
        Game toJoin = lobbyRepository.getById(gameId);
        if (toJoin == null) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        GamePlayer player = new GamePlayer(playerData);
        boolean success = toJoin.add(player);
        lobbyRepository.save(toJoin);
        return new ResponseEntity<>(success, success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
