package server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static server.utils.TestHelpers.getUUID;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.services.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void addReaction() {
        reactionService.addReaction("test", getUUID(3));
        assertEquals(1, reactionService.getReactions().size());
    }

    @Test
    void removeReaction() {
        reactionService.addReaction("test", getUUID(3));
        assertTrue(reactionService.removeReaction("test"));
        assertEquals(0, reactionService.getReactions().size());
    }

    @Test
    void removeReaction_notExists() {
        assertFalse(reactionService.removeReaction("test"));
    }

    @Test
    void getReactionURLs() {
        when(storageService.getURI(any(UUID.class)))
                .thenReturn(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));

        reactionService.addReaction("test", getUUID(3));
        Map<String, URI> urls = reactionService.getReactionURLs();
        assertEquals(1, urls.size());
        assertEquals(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"), urls.get("test"));
    }
}