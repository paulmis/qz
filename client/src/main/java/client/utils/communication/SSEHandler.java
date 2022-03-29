package client.utils.communication;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static javafx.application.Platform.runLater;

import commons.entities.messages.SSEMessageType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;


/**
 * This class handles everything SSE related.
 * It handles events, exceptions and the end of a connection.
 */
@Generated
@Slf4j
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

    Object handlerSource = null;
    SseEventSource sseEventSource;
    Map<SSEMessageType, SSEEventHandler> eventHandlers;

    /**
     * No-args constructor.
     */
    public SSEHandler() {
        eventHandlers = new HashMap<>();
        eventHandlers.put(SSEMessageType.INIT, inboundSseEvent -> log.info("--[SSE]-- SSE handler initialized"));
    }

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
     * This function subscribes to the SSE event source.
     * It calls the SSE open endpoint and handles the events.
     */
    public void subscribe() {
        if (sseEventSource != null) {
            log.warn("--[SSE]-- SSEHandler already subscribed!");
        }

        // Builds the event source with the target.
        SseEventSource eventSource = SseEventSource
            .target(
                ServerUtils
                    .getRequestTarget()
                    .path("/api/sse/open"))
            .reconnectingEvery(0, MICROSECONDS).build();

        // Registers the handling of events, exceptions and completion.
        eventSource.register(
            this::handleEvent,
            this::handleException,
            this::handleCompletion);

        // Opens the sse listener and sets the source of the events in the handler
        eventSource.open();
        setSseEventSource(eventSource);
    }

    /**
     * Asynchronously kills the current SSE connection.
     */
    public void kill() {
        new Thread(() -> {
            log.info("--[SSE]-- Killing the SSE connection...");
            this.sseEventSource.close();
            log.info("--[SSE]-- Killed the SSE connection");
        }).start();
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
        log.info("--[SSE]-- Initializing handler with " + handlerSource.getClass().getName());

        // Check that the source class implements SSESource
        if (!SSESource.class.isAssignableFrom(handlerSource.getClass())) {
            throw new IllegalArgumentException("The source class must extend from SSESource");
        }

        // Gets all the methods that have the Name decoration. These are the event handlers.
        List<Method> handlers = ReflectionUtils.getAnnotatedMethods(handlerSource,
                client.utils.communication.SSEEventHandler.class);

        // We get the names of all the event handlers
        List<SSEMessageType> names = handlers.stream()
                .map(ReflectionUtils::getSSEEventName).collect(Collectors.toList());

        // We generate the runnables for each event.
        List<SSEEventHandler> runnables = handlers.stream().map(method -> {
            // This gets the types of the parameters of the method.
            Class<?>[] types = method.getParameterTypes();

            // throws exception if the length is different from 1
            if (types.length > 1) {
                throw new IllegalStateException("The class " + method.getName() + " doesn't have the required number"
                        + "of parameters.");
            }

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
                } else {
                    // Reads the object with the extracted type.
                    Object obj = inboundSseEvent.readData(types[0]);

                    // This invokes the function with the object and the source inside a run later so
                    // javafx components can have their state changed.
                    runLater(() -> {
                        try {
                            method.invoke(handlerSource, obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

            };
        }).collect(Collectors.toList());

        // This zips the names and runnables together into the map.
        eventHandlers = IntStream.range(0, names.size()).boxed()
                .collect(Collectors.toMap(names::get, runnables::get));

        eventHandlers.put(SSEMessageType.INIT, inboundSseEvent -> log.info("--[SSE]-- SSE handler initialized"));
    }

    /**
     * This function sets the event source. Should be used by the
     * function that opens the sse connection.
     *
     * @param sseEventSource the source of the sse events.
     */
    public void setSseEventSource(SseEventSource sseEventSource) {
        this.sseEventSource = sseEventSource;
        log.info("Assigned SSE source");
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
        log.info("--[SSE]-- Handle event " + inboundSseEvent.getName());
        try {
            if (eventHandlers.containsKey(SSEMessageType.valueOf(inboundSseEvent.getName()))) {
                eventHandlers.get(SSEMessageType.valueOf(inboundSseEvent.getName())).handle(inboundSseEvent);
            } else {
                log.error("--[SSE]-- No handler for event " + inboundSseEvent.getName());
                log.error("--[SSE]-- Source class:"
                    + (this.handlerSource == null
                        ? "<null>"
                        : this.handlerSource.getClass().getName())
                    + " with events:");
                for (SSEMessageType message : eventHandlers.keySet()) {
                    System.out.println("    " + message.toString());
                }
            }
        } catch (Exception e) {
            log.error("--[SSE]-- Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleException(Throwable throwable) {
        log.error("--[SSE]-- Exception encountered ");
        throwable.printStackTrace();
    }

    public void handleCompletion() {
        log.error("--[SSE]-- Completed");
    }
}

