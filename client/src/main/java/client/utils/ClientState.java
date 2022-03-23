package client.utils;

import commons.entities.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.questions.QuestionDTO;

/**
 * A singleton representing the current user and game state.
 */
public class ClientState {
    public static GameDTO game;
    public static QuestionDTO currentQuestion;
    public static UserDTO user;
}
