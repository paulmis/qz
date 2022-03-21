package server.database.entities.utils;

import java.util.Date;
import lombok.Data;

/**
 * Describe a callback + execution time for a game stage.
 */
@Data
public class GameStage {
    private Runnable callback;
    private Date executionTime;
}
