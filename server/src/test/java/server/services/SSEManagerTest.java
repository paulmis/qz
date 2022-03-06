package server.services;

import static org.junit.jupiter.api.Assertions.*;
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
        sseManager = new SSEManager();
    }

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + id);
    }

    @Test
    void testRegisterOne() {
        sseManager.register(getUUID(1), new SseEmitter());
        assertEquals(1, sseManager.getNumberOfEmitters());
    }

    @Test
    void testRegisterMultiple() {
        sseManager.register(getUUID(1), new SseEmitter());
        sseManager.register(getUUID(2), new SseEmitter());
        sseManager.register(getUUID(3), new SseEmitter());
        assertEquals(3, sseManager.getNumberOfEmitters());
    }

    @Test
    void testRegisterDuplicate() {
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(1), emitter2);
        assertEquals(1, sseManager.getNumberOfEmitters());
    }

    @Test
    void testUnregisterOne() {
        sseManager.register(getUUID(1), new SseEmitter());
        sseManager.register(getUUID(2), new SseEmitter());
        sseManager.register(getUUID(3), new SseEmitter());

        assertTrue(sseManager.unregister(getUUID(2)));
        assertEquals(2, sseManager.getNumberOfEmitters());
    }

    @Test
    void testUnregisterUnknown() {
        assertFalse(sseManager.unregister(getUUID(1)));
    }

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

    @Test
    void testIsRegistered() {
        SseEmitter emitter1 = new SseEmitter();
        sseManager.register(getUUID(1), emitter1);
        assertTrue(sseManager.isRegistered(getUUID(1)));
        assertFalse(sseManager.isRegistered(getUUID(2)));
    }

    @Test
    void testSendOne() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter2 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(2), emitter2);

        assertTrue(sseManager.send(getUUID(1), "test"));

        verify(emitter1, times(1)).send(any());
        verify(emitter2, never()).send(any());
    }

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

    @Test
    void testSendUnknown() throws IOException {
        assertFalse(sseManager.send(getUUID(1), "test"));
    }

    @Test
    void testSendMultipleUnknown() throws IOException {
        SseEmitter emitter1 = Mockito.spy(new SseEmitter());
        SseEmitter emitter3 = Mockito.spy(new SseEmitter());
        sseManager.register(getUUID(1), emitter1);
        sseManager.register(getUUID(3), emitter3);

        Set<UUID> uuids = Set.of(getUUID(1), getUUID(2));

        assertFalse(sseManager.send(uuids, "test"));

        verify(emitter1, times(1)).send(any());
        verify(emitter3, never()).send(any());
    }

    @Test
    void testGetNumberOfEmitters() {
        sseManager.register(getUUID(1), new SseEmitter());
        assertEquals(1, sseManager.getNumberOfEmitters());
        sseManager.register(getUUID(2), new SseEmitter());
        assertEquals(2, sseManager.getNumberOfEmitters());
    }
}