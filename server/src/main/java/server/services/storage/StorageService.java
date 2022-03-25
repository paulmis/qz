package server.services.storage;

import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.Generated;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

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
     * @param file File to store.
     */
    void store(MultipartFile file);

    /**
     * Get a stream of all stored file paths.
     *
     * @return Stream of stored file paths.
     */
    Stream<Path> loadAll();

    /**
     * Resolve a file path to a resource.
     *
     * @param filename file path.
     * @return resource file path.
     */
    Path load(String filename);

    /**
     * Load a file as a resource.
     *
     * @param filename file path.
     * @return loaded resource.
     */
    Resource loadAsResource(String filename);

    /**
     * Delete all stored files.
     */
    void deleteAll();
}
