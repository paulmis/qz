package client.utils;

import commons.entities.GameStage;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.questions.QuestionDTO;
import java.util.Optional;
import lombok.Getter;

/**
 * A singleton representing the current user and game state.
 */
@Getter
public class ClientState {
    public static GameDTO game;
    public static GameStage gameStage;
    public static QuestionDTO currentQuestion;
    public static UserDTO user;
    public static Optional<Integer> previousScore = Optional.empty();
}
