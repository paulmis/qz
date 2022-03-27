package server.database.entities.game;

import commons.entities.game.GameDTO;
import lombok.Generated;

/**
 * Mock Game subclass for testing purposes.
 */
@Generated
public class MockGame extends Game<GameDTO> {
    public boolean isLastQuestion() {
        return false;
    }

    public boolean shouldFinish() {
        return false;
    }

    public GameDTO getDTO() {
        return super.toDTO();
    }
}
