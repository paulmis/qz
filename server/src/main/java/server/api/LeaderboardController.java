package server.api;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.UserDTO;
import commons.entities.utils.Views;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
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

    public static final int MAX_PAGE_SIZE = 50;

    /**
     * Get the  users sorted by score.
     *
     * @param page specifies the page of the leaderboard to be returned.
     * @param size specifies the size of the leaderboard page to be returned.
     * @return a list of users sorted by their score in descending order.
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/score")
    public ResponseEntity<List<UserDTO>> getScoreLeaderboard(@RequestParam Optional<Integer> page,
                                                             @RequestParam Optional<Integer> size) {
        Pageable paging = PageRequest.of(page.orElse(0), Math.min(MAX_PAGE_SIZE, size.orElse(MAX_PAGE_SIZE)));
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByScoreDesc(paging).stream()
                .map(User::getDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }

    /**
     * Get the  users sorted by number of played games.
     *
     * @param page specifies the page of the leaderboard to be returned.
     * @param size specifies the size of the leaderboard page to be returned.
     * @return a list of users sorted by the number of played games in descending order.
     */
    @JsonView(Views.Public.class)
    @RequestMapping("/games")
    public ResponseEntity<List<UserDTO>> getGamesLeaderboard(@RequestParam Optional<Integer> page,
                                                             @RequestParam Optional<Integer> size) {
        Pageable paging = PageRequest.of(page.orElse(0), Math.min(MAX_PAGE_SIZE, size.orElse(MAX_PAGE_SIZE)));
        List<UserDTO> userLeaderboard = userRepository.findAllByOrderByGamesPlayedDesc(paging).stream()
                .map(User::getDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userLeaderboard);
    }
}
