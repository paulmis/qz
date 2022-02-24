package server.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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
public class LobbyController {

    /**
     * repository of the lobbies.
     */
    private final GameRepository lobbyRepo;

    /**
     * Constructor of the LobbyController.
     *
     * @param lobbyRepo the game repository.
     */
    LobbyController(GameRepository lobbyRepo) {
        this.lobbyRepo = lobbyRepo;
    }

    /**
     * endpoint for available lobbies.
     *
     * @return a list of available lobbies.
     */
    @GetMapping("/lobby")
    List<Game> availableLobbies() {
        // It should return games with status Created
        List<Game> lobbies = lobbyRepo.findAll();
        lobbies.removeIf(game -> game.getStatus() == GameStatus.CREATED);
        return lobbies;
    }
}
