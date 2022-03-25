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
    @NonNull private Optional<ScheduledFuture<?>> future;
    @NonNull private Date scheduledDate;
}
