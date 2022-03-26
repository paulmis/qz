package server.services.storage;

import java.net.URI;
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
     * @return stored file path.
     */
    Path store(MultipartFile file);

    /**
     * Store a file in the storage service.
     *
     * @param file File to store.
     * @param filename Filename to store the file as.
     * @return stored file path.
     */
    Path store(MultipartFile file, String filename);

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

    /**
     * Get the URI of a file.
     *
     * @param filename file path.
     * @return URI of the file.
     */
    URI getURI(String filename);
}
