package server.services.storage;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Generated;
import org.springframework.core.io.Resource;

/**
 * Storage service interface.
 */
@Generated
public interface StorageService {
    /**
     * Initialize the storage service.
     */
    void init();

    /**
     * Store a file in the storage service.
     *
     * @param fileStream File stream to store.
     * @return resource ID.
     */
    UUID store(InputStream fileStream);

    /**
     * Store a file in the storage service.
     *
     * @param fileStream File stream to store.
     * @param resourceId Resource ID to store the file as.
     * @return resource ID.
     */
    UUID store(InputStream fileStream, UUID resourceId);

    /**
     * Get a stream of all stored file paths.
     *
     * @return Stream of stored file paths.
     */
    Stream<Path> loadAll();

    /**
     * Load a file as a resource.
     *
     * @param resourceId ID of the resource to load.
     * @return loaded resource.
     */
    Resource loadAsResource(UUID resourceId);

    /**
     * Delete all stored files.
     */
    void deleteAll();

    /**
     * Get the URI of a resource.
     *
     * @param resourceId resource ID.
     * @return URI of the resource.
     */
    URI getURI(UUID resourceId);
}
