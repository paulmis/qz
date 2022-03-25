package server.services.fsm;

/**
 * All available FSM states.
 */
public enum FSMState {
    /**
     * The game has not been started yet.
     */
    IDLE,
    /**
     * The game is currently accepting answers.
     */
    QUESTION,
    /**
     * The game is currently showing the correct answer.
     */
    ANSWER,
    /**
     * The game is currently showing the leaderboard.
     */
    LEADERBOARD,
    /**
     * The game is finished.
     */
    FINISHED
}
