package server.api;

import commons.entities.ActivityDTO;
import commons.entities.game.GameDTO;
import commons.entities.game.GameStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.User;
import server.database.entities.game.Game;
import server.database.entities.question.Activity;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;
import server.database.repositories.UserRepository;
import server.database.repositories.game.GameRepository;
import server.database.repositories.question.ActivityRepository;
import server.database.repositories.question.QuestionRepository;
import server.services.ActivityService;

/**
 * Endpoints for testing and debugging.
 */
@RestController
@RequestMapping("/api/test")
public class DebugController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/dbsetup")
    ResponseEntity setupDb() {
        try {
            // Generate Activities
            generateActivities();

            // Generate Questions
            generateQuestions();

            // Generate Users
            generateUsers();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Auto-generate questions.
     *
     * @return 200 when done
     */
    @PostMapping("/demo")
    ResponseEntity generateQuestions() {
        List<Activity> activities = activityRepository.findByAbandonedIsFalse();
        gameRepository.deleteAll();
        questionRepository.deleteAll();

        final int numQuestionsPerType = 30;

        // MC Questions
        for (int idx = 0; idx < numQuestionsPerType; idx++) {
            MCQuestion newQuestion = new MCQuestion();
            Activity answer = activities.get((new Random()).nextInt(activities.size()));
            newQuestion.setAnswer(answer);
            List<Activity> possibleOptions = activities.stream()
                    .filter(act ->
                            act.getCost() < answer.getCost() * 1.15
                            && act.getCost() > answer.getCost() * 0.85
                            && act.getDescription().contains("ing"))
                    .collect(Collectors.toList());
            if (possibleOptions.size() < 3) {
                idx--;
                continue;
            }
            List<Activity> usedOptions = new ArrayList<>(List.of(answer));
            for (int optIdx = 0; optIdx < possibleOptions.size(); optIdx++) {
                boolean optionOk = true;
                Activity option = possibleOptions.get(optIdx);
                for (int usedIdx = 0; usedIdx < usedOptions.size(); usedIdx++) {
                    if (option.getCost() == usedOptions.get(usedIdx).getCost()) {
                        optionOk = false;
                        break;
                    }
                }
                if (optionOk) {
                    usedOptions.add(option);
                    if (usedOptions.size() == 4) {
                        break;
                    }
                }
            }
            if (usedOptions.size() < 4) {
                idx--;
                continue;
            }
            Collections.shuffle(usedOptions);
            newQuestion.setActivities(usedOptions);

            newQuestion.setGuessConsumption(idx % 2 == 0);
            if (newQuestion.isGuessConsumption()) {
                newQuestion.setText(answer.getDescription() + " consumes...");
            } else {
                newQuestion.setText("Which one(s) of these activities consume " + answer.getCost() + " Wh?");
            }
            questionRepository.save(newQuestion);
        }

        // Estimate Questions
        for (int idx = 0; idx < numQuestionsPerType; idx++) {
            EstimateQuestion newQuestion = new EstimateQuestion();
            Activity answer = activities.get((new Random()).nextInt(activities.size()));
            newQuestion.setActivities(List.of(answer));
            newQuestion.setText("How much does " + answer.getDescription() + " consumes approximately?");
            questionRepository.save(newQuestion);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Auto-generate activities.
     *
     * @throws Exception any exception
     */
    private void generateActivities() throws Exception {
        for (int idx = (int) activityRepository.count(); idx < 100; idx++) {
            try {
                ActivityDTO activity = new ActivityDTO();
                activity.setDescription("Activity" + idx);
                activity.setCost(20 + idx * 4L);
                activityRepository.save(new Activity(activity));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Auto-generate users.
     *
     * @throws Exception any exception
     */
    private void generateUsers() throws Exception {
        for (int idx = (int) userRepository.count(); idx < 4; idx++) {
            User user = new User();
            String name = "" + ((char) ('A' + idx));
            user.setUsername("username" + name);
            user.setEmail("student" + name + "@tudelft.nl");
            user.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user);
        }
    }

    @GetMapping("/all")
    ResponseEntity<List<GameDTO>> allGames() {
        // It returns games with status Created
        List<GameDTO> lobbies = gameRepository
                .findAll()
                .stream()
                .map(Game::getDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lobbies);
    }

    @PutMapping("/all/kill")
    ResponseEntity killAllGames() {
        List<Game> lobbies = gameRepository.findAll();
        for (Game lobby : lobbies) {
            lobby.setStatus(GameStatus.FINISHED);
            gameRepository.save(lobby);
        }
        return ResponseEntity.ok().build();
    }
}
