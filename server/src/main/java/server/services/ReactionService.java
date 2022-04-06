package server.services;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.game.Reaction;
import server.database.repositories.game.ReactionRepository;
import server.services.storage.StorageService;

/**
 * Service for managing reactions.
 */
@Slf4j
@Service
public class ReactionService {
    @Autowired
    private StorageService storageService;

    @Autowired
    private ReactionRepository reactionRepository;

    /**
     * Add a reaction to the map.
     *
     * @param reaction   The reaction to add.
     * @param resourceId The resource id.
     * @return Name of the saved reaction.
     */
    public String addReaction(String reaction, UUID resourceId) {
        log.debug("Adding reaction {}", reaction);
        return reactionRepository.save(new Reaction(reaction, resourceId)).getName();
    }

    /**
     * Remove a reaction from the map.
     *
     * @param reaction The reaction to remove.
     * @return Whether the reaction was removed or not.
     */
    public boolean removeReaction(String reaction) {
        log.debug("Removing reaction {}", reaction);
        return reactionRepository.deleteByNameEquals(reaction) > 0L;
    }

    /**
     * Get all saved reactions.
     *
     * @return all reactions.
     */
    public List<Reaction> getReactions() {
        log.trace("Getting reactions");
        return reactionRepository.findAll();
    }

    /**
     * Get map of reactions and corresponding image URLs.
     *
     * @return map of reactions and corresponding image URLs.
     */
    public Map<String, URI> getReactionURLs() {
        log.debug("Getting reaction URLs");
        return getReactions().stream().collect(Collectors.toMap(
                Reaction::getName,
                e -> storageService.getURI(e.getImageId())
        ));
    }
}
