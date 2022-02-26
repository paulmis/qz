package server.api;

import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        // It should return games with status Created
        List<GameDTO> lobbies = lobbyRepository.findAllByStatus(GameStatus.CREATED)
                .stream().map(g -> g.getDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(lobbies, HttpStatus.OK);
    }
}
