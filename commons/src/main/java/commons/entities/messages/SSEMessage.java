package commons.entities.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SSE message structure.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SSEMessage {
    /**
     * Message type.
     */
    @NonNull
    protected SSEMessageType type;

    /**
     * Message payload.
     */
    protected Object data;
}
