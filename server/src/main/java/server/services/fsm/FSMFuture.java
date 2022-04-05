package server.services.fsm;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import lombok.Data;
import lombok.NonNull;

/**
 * A future that can be used to cancel a scheduled task.
 */
@Data
public class FSMFuture {
    /**
     * The future that can be used to cancel the task.
     */
    @NonNull private Optional<ScheduledFuture<?>> future;

    /**
     * The time at which the task was scheduled.
     */
    @NonNull private Date scheduledDate;

    @NonNull private Runnable runnable;
}
