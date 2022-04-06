package server.services;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.services.storage.StorageService;

/**
 * Service for managing reactions.
 */
@Slf4j
@Service
public class ReactionService {
    private final Map<String, UUID> reactions = new HashMap<>();
    @Autowired
    private StorageService storageService;

    /**
     * Add a reaction to the map.
     *
     * @param reaction   The reaction to add.
     * @param resourceId The resource id.
     */
    public void addReaction(String reaction, UUID resourceId) {
        log.debug("Adding reaction {}", reaction);
        reactions.put(reaction, resourceId);
    }

    /**
     * Remove a reaction from the map.
     *
     * @param reaction The reaction to remove.
     * @return Whether the reaction was removed or not.
     */
    public boolean removeReaction(String reaction) {
        log.debug("Removing reaction {}", reaction);
        return reactions.remove(reaction) != null;
    }

    /**
     * Get map of reactions and corresponding image URLs.
     *
     * @return map of reactions and corresponding image URLs.
     */
    public Map<String, URI> getReactionURLs() {
        log.trace("Getting reaction URLs");
        return reactions.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> storageService.getURI(e.getValue())
        ));
    }
}
