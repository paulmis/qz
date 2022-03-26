package client.utils.communication;

import static javafx.application.Platform.runLater;

import commons.entities.messages.SSEMessageType;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import lombok.Generated;


/**
 * This class handles everything SSE related.
 * It handles events, exceptions and the end of a connection.
 */
@Generated
public class SSEHandler {

    /**
     * The interface that encompasses
     * the function that is to be called on a specific event.
     */
    public interface SSEEventHandler {
        /**
         * The function that handles the sse event.
         *
         * @param inboundSseEvent the inbound sse event that the server sent.
         */
        void handle(InboundSseEvent inboundSseEvent);
    }

    Object handlerSource;
    SseEventSource sseEventSource;
    Map<SSEMessageType, SSEEventHandler> eventHandlers;

    /**
     * No-args constructor.
     */
    public SSEHandler() {}

    /**
     * The constructor of the SSEHandler.
     * It initializes a map from event name
     * to SSEEventHandler and automatically converts the
     * sent object to the required object in the class.
     *
     * @param handlerSource the handler source object. This is the source of the events handlers.
     */
    public SSEHandler(Object handlerSource) {
        initialize(handlerSource);
    }


    /**
     * This function initializes the sse handler object.
     * It resets the map and changes the source object.
     *
     * @param handlerSource the source of the handlers.
     * @throws IllegalArgumentException if the source doesn't implement SSESource
     */
    public void initialize(Object handlerSource) throws IllegalArgumentException {
        this.handlerSource = handlerSource;

        // Check that the source class implements SSESource
        if (!SSESource.class.isAssignableFrom(handlerSource.getClass())) {
            throw new IllegalArgumentException("The source class must extend from SSESource");
        }

        // Gets all the methods that have the Name decoration. These are the event handlers.
        var handlers = ReflectionUtils.getAnnotatedMethods(handlerSource,
                client.utils.communication.SSEEventHandler.class);

        // We get the names of all the event handlers
        var names = handlers.stream().map(ReflectionUtils::getSSEEventName).collect(Collectors.toList());

        // We generate the runnables for each event.
        var runnables = handlers.stream().map(method -> {
            // This gets the types of the parameters of the method.
            var types = method.getParameterTypes();

            // throws exception if the length is different from 1
            if (types.length > 1) {
                throw new IllegalStateException("The class " + method.getName() + " doesn't have the required number"
                        + "of parameters.");
            }

            // Gets the type of the first parameter

            // This creates the SSEEvent handler for the event.
            return (SSEEventHandler) inboundSseEvent -> {

                // Calls the method directly if there are no parameters.
                if (types.length == 0) {
                    runLater(() -> {
                        try {
                            method.invoke(handlerSource);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                // Reads the object with the extracted type.
                var obj = inboundSseEvent.readData(types[0]);

                // This invokes the function with the object and the source inside a run later so
                // javafx components can have their state changed.
                runLater(() -> {
                    try {
                        method.invoke(handlerSource, obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            };
        }).collect(Collectors.toList());

        // This zips the names and runnables together into the map.
        eventHandlers = IntStream.range(0, names.size()).boxed()
                .collect(Collectors.toMap(names::get, runnables::get));

        eventHandlers.put(SSEMessageType.INIT, new SSEEventHandler() {
            @Override
            public void handle(InboundSseEvent inboundSseEvent) {
                System.out.println("Initialized connection SSE.");
            }
        });
    }

    /**
     * This function sets the event source. Should be used by the
     * function that opens the sse connection.
     *
     * @param sseEventSource the source of the sse events.
     */
    public void setSseEventSource(SseEventSource sseEventSource) {
        this.sseEventSource = sseEventSource;
    }

    /**
     * Checks if the connection is open.
     *
     * @return a boolean that tells if the connection is open or not.
     */
    public boolean isConnectionOpen() {
        return sseEventSource.isOpen();
    }

    /**
     * This handles the inbound sse events.
     * It calls the required method inside the map.
     *
     * @param inboundSseEvent the sse event.
     */
    public void handleEvent(InboundSseEvent inboundSseEvent) {
        eventHandlers.get(SSEMessageType.valueOf(inboundSseEvent.getName())).handle(inboundSseEvent);
    }

    public void handleException(Throwable throwable) {

    }

    public void handleCompletion() {

    }
}

