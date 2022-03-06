package server.api;

import commons.entities.UserDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @RequestMapping("/score")
    public ResponseEntity<List<UserDTO>> getScoreLeaderboard() {
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByScoreDesc().stream().map(u -> {
            UserDTO dto = u.getDTO();
            u.setEmail(null);
            u.setPassword(null);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }

    /**
     * Get the  users sorted by score.
     *
     * @return a list of users sorted by their score in descending order.
     */
    @RequestMapping("/score")
    public ResponseEntity<List<UserDTO>> getGamesLeaderboard() {
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByGamesPlayedDesc().stream().map(u -> {
            UserDTO dto = u.getDTO();
            u.setEmail(null);
            u.setPassword(null);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }
}
