package server.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {
    @Test
    void testType() {
        // Verify that the exception is indeed a RuntimeException
        ResourceNotFoundException e = new ResourceNotFoundException("message");
        assertInstanceOf(RuntimeException.class, e);
    }

    @Test
    void testConstructorMessage() {
        // Verify that the message is set correctly
        ResourceNotFoundException e = new ResourceNotFoundException("message");
        assertEquals("message", e.getMessage());
    }

    @Test
    void testConstructorMessageCause() {
        // Verify that the message and cause are set correctly
        ResourceNotFoundException e = new ResourceNotFoundException("message", new Exception("test"));
        assertEquals("message", e.getMessage());
        assertEquals("test", e.getCause().getMessage());
    }
}