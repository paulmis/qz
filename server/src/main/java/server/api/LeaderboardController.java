package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.UserDTO;
import commons.entities.utils.Views;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.utils.BaseEntity;
import server.database.repositories.UserRepository;

/**
 * Controller for leaderboard API endpoints.
 */
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    @Autowired
    private UserRepository userRepository;

    /**
     * Get the  users sorted by score.
     *
     * @return a list of users sorted by their score in descending order.
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/score")
    public ResponseEntity<List<UserDTO>> getScoreLeaderboard() {
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByScoreDesc().stream()
                .map(BaseEntity::getDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }

    /**
     * Get the  users sorted by score.
     *
     * @return a list of users sorted by their score in descending order.
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/games")
    public ResponseEntity<List<UserDTO>> getGamesLeaderboard() {
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByGamesPlayedDesc().stream()
                .map(BaseEntity::getDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }
}