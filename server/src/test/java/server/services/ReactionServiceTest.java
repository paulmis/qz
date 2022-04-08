package server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static server.utils.TestHelpers.getUUID;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.game.Reaction;
import server.database.repositories.game.ReactionRepository;
import server.services.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void addReaction() {
        when(reactionRepository.save(any(Reaction.class))).thenReturn(new Reaction("test", getUUID(3)));

        reactionService.addReaction("test", getUUID(3));
        verify(reactionRepository, times(1)).save(any(Reaction.class));
    }

    @Test
    void removeReaction() {
        when(reactionRepository.save(any(Reaction.class))).thenReturn(new Reaction("test", getUUID(3)));
        when(reactionRepository.deleteByNameEquals(any(String.class))).thenReturn(1L);

        reactionService.addReaction("test", getUUID(3));
        assertTrue(reactionService.removeReaction("test"));
    }

    @Test
    void removeReaction_notExists() {
        when(reactionRepository.deleteByNameEquals(any(String.class))).thenReturn(0L);

        assertFalse(reactionService.removeReaction("test"));
    }

    @Test
    void getReactionURLs() {
        when(storageService.getURI(any(UUID.class)))
                .thenReturn(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        when(reactionRepository.findAll()).thenReturn(List.of(new Reaction("test", getUUID(3))));
        when(reactionRepository.save(any(Reaction.class))).thenReturn(new Reaction("test", getUUID(3)));

        reactionService.addReaction("test", getUUID(3));
        Map<String, URI> urls = reactionService.getReactionURLs();
        assertEquals(1, urls.size());
        assertEquals(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"), urls.get("test"));
    }
}