package server.configuration.quiz;

import lombok.Data;

/**
 * Timing related configuration for the game.
 */
@Data
public class QuizTimingConfiguration {
    /**
     * Length of the preparation phase.
     */
    private int preparationTime = 5000;

    /**
     * Length of the answer phase (how long is the answer shown on screen).
     */
    private int answerTime = 8000;

    /**
     * Length of the leaderboard phase (how long is the leaderboard shown on screen).
     */
    private int leaderboardTime = 8000;
}
