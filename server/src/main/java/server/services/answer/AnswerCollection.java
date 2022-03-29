package server.services.answer;

import commons.entities.AnswerDTO;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Collection of answers (for a specific question).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerCollection {
    private Map<UUID, AnswerDTO> answers = new ConcurrentHashMap<>();

    /**
     * Check whether a player has answered the question.
     *
     * @param playerId Player ID.
     * @return True if the player has submitted an answer.
     */
    public boolean hasAnswer(UUID playerId) {
        return answers.containsKey(playerId);
    }

    /**
     * Submit a player's answer.
     *
     * @param playerId Player's ID.
     * @param answer Player's answer.
     */
    public void addAnswer(UUID playerId, AnswerDTO answer) {
        answers.put(playerId, answer);
    }

    /**
     * Get the answer of a player.
     *
     * @param playerId The player's ID.
     * @return The answer submitted by the player.
     */
    public AnswerDTO getAnswer(UUID playerId) {
        return answers.get(playerId);
    }

    /**
     * Get all submitted answers.
     *
     * @return The set of players who have submitted an answer and their answers.
     */
    public Set<Map.Entry<UUID, AnswerDTO>> getAnswers() {
        return answers.entrySet();
    }
}
