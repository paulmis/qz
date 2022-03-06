package server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class SSEManagerTest {
    private SSEManager sseManager;

    @BeforeEach
    void setUp() {
        // We want a new SSEManager for each test run.
        sseManager = new SSEManager();
    }

    /**
     * Convert an integer to a UUID.
     *
     * @param id ID to convert to UUID.
     * @return Generated UUID.
     */
    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + id);
    }

    /**
     * Try registering one emitter.
     */
    @Test
    void testRegisterOne() {
        sseManager.register(getUUID(1), new SseEmitter());
        // Verify that one emitter is registered.
        assertEquals(1, sseManager.getNumberOfEmitters());
    }

    /**
     * Try registering multiple emitters.
     */
    @Test
    void testRegisterMultiple() {
        sseManager.register(getUUID(1), new SseEmitter());
        sseManager.register(getUUID(2), new SseEmitter());
        sseManager.register(getUUID(3), new SseEmitter());
        // Verify that all emitters are registered.
        assertEquals(3, sseManager.getNumberOfEmitters());
    }

    /**
     * Try registering two emitters for the same user.
     * In this scenario, the expected behavior is that the old emitter is closed and replaced by the new emitter.
     */
    @Test
    void testRegisterDuplicate() {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter2 = new SseEmitter();
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(1), emitter2);

        // Verify that the old emitter was closed.
        verify(emitter1, times(1)).complete();
        // Verify that only one emitter is registered for the user.
        assertEquals(1, sseManager.getNumberOfEmitters());
    }

    /**
     * Try unregistering one emitter.
     */
    @Test
    void testUnregisterOne() {
        sseManager.register(getUUID(1), new SseEmitter());
        sseManager.register(getUUID(2), new SseEmitter());
        sseManager.register(getUUID(3), new SseEmitter());

        // Verify that emitter has been unregistered successfully.
        assertTrue(sseManager.unregister(getUUID(2)));
        // Verify that the number of emitters is correct.
        assertEquals(2, sseManager.getNumberOfEmitters());
    }

    /**
     * Try unregistering one emitter that is not registered.
     */
    @Test
    void testUnregisterUnknown() {
        assertFalse(sseManager.unregister(getUUID(1)));
    }

    /**
     * Try getting emitters by user ID.
     */
    @Test
    void testGet() {
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();
        SseEmitter emitter3 = new SseEmitter();

        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(2), emitter2);
        sseManager.register(getUUID(3), emitter3);

        assertEquals(emitter1, sseManager.get(getUUID(1)));
        assertEquals(emitter2, sseManager.get(getUUID(2)));
        assertEquals(emitter3, sseManager.get(getUUID(3)));
    }

    /**
     * Verify that UUID registrations are properly detected.
     */
    @Test
    void testIsRegistered() {
        SseEmitter emitter1 = new SseEmitter();
        sseManager.register(getUUID(1), emitter1);
        assertTrue(sseManager.isRegistered(getUUID(1)));
        assertFalse(sseManager.isRegistered(getUUID(2)));
    }

    /**
     * Try sending a message to one emitter. Only this emitter should have its send() function called.
     *
     * @throws IOException If an error occurs.
     */
    @Test
    void testSendOne() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter2 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(2), emitter2);

        assertTrue(sseManager.send(getUUID(1), "test"));

        // Verify that the `send()` function was called only for the emitter with the given ID.
        verify(emitter1, times(1)).send(any());
        verify(emitter2, never()).send(any());
    }

    /**
     * Try sending a message to multiple emitters. Only targeted emitters should have their send() function called.
     *
     * @throws IOException If an error occurs.
     */
    @Test
    void testSendMultiple() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter2 = Mockito.spy(new SseEmitter());
        SseEmitter emitter3 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(2), emitter2);
        sseManager.register(getUUID(3), emitter3);

        Set<UUID> uuids = Set.of(getUUID(1), getUUID(2));

        assertTrue(sseManager.send(uuids, "test"));

        verify(emitter1, times(1)).send(any());
        verify(emitter2, times(1)).send(any());
        verify(emitter3, never()).send(any());
    }

    /**
     * Try sending a message to all registered emitters.
     *
     * @throws IOException If an error occurs.
     */
    @Test
    void testSendAll() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter2 = Mockito.spy(new SseEmitter());
        SseEmitter emitter3 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(2), emitter2);
        sseManager.register(getUUID(3), emitter3);

        sseManager.sendAll("test");

        verify(emitter1, times(1)).send(any());
        verify(emitter2, times(1)).send(any());
        verify(emitter3, times(1)).send(any());
    }

    /**
     * Try sending a message to a user who doesn't have an emitter registered.
     *
     * @throws IOException If an error occurs.
     */
    @Test
    void testSendUnknown() throws IOException {
        // Function should return false.
        assertFalse(sseManager.send(getUUID(1), "test"));
    }

    /**
     * Try sending a message to multiple users, one of which doesn't have a registered emitter.
     *
     * @throws IOException If an error occurs.
     */
    @Test
    void testSendMultipleUnknown() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter3 = Mockito.spy(new SseEmitter());
        SseEmitter emitter4 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(3), emitter3);
        sseManager.register(getUUID(4), emitter4);

        Set<UUID> uuids = Set.of(getUUID(1), getUUID(2), getUUID(3));

        // Function call should return false, because we have a user without an emitter in the set.
        assertFalse(sseManager.send(uuids, "test"));

        // All users that were in the set and have emitters registered should be notified.
        verify(emitter1, times(1)).send(any());
        verify(emitter3, times(1)).send(any());
        verify(emitter4, never()).send(any());
    }

    @Test
    void testGetNumberOfEmitters() {
        sseManager.register(getUUID(1), new SseEmitter());
        assertEquals(1, sseManager.getNumberOfEmitters());
        sseManager.register(getUUID(2), new SseEmitter());
        assertEquals(2, sseManager.getNumberOfEmitters());
    }
}