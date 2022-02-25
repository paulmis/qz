package server.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.game.Game;
import server.database.entities.game.GameStatus;
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
    ResponseEntity<List<Game>> testEndpoint(
            @RequestParam(required = false) Integer id, @PathVariable(required = false) String param) {
        System.out.println(param + ", " + id);
        List<Game> lobbies = lobbyRepository.findAllByStatus(GameStatus.CREATED);
        if (id == 42) {
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
    ResponseEntity<List<Game>> availableLobbies() {
        // It should return games with status Created
        List<Game> lobbies = lobbyRepository.findAllByStatus(GameStatus.CREATED);
        return new ResponseEntity<>(lobbies, HttpStatus.OK);
    }
}
