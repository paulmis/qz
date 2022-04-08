package server.services.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import server.configuration.FileSystemStorageConfiguration;
import server.configuration.ResourceConfiguration;
import server.exceptions.ResourceNotFoundException;
import server.exceptions.StorageException;

/**
 * Service for filesystem storage.
 */
@Slf4j
@Service
public class FileSystemStorageService implements StorageService {
    @Autowired
    private FileSystemStorageConfiguration fsConfiguration;

    @Autowired
    private ResourceConfiguration resourceConfiguration;

    /**
     * Initialize the storage service.
     */
    @Override
    public void init() {
        try {
            Files.createDirectories(this.fsConfiguration.getUploadDirPath());
        } catch (IOException e) {
            log.error("Could not initialize storage", e);
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Store a file in the storage service.
     *
     * @param fileStream File stream to store.
     * @return resource ID.
     */
    @Override
    public UUID store(InputStream fileStream) {
        return store(fileStream, UUID.randomUUID());
    }

    /**
     * Store a file in the storage service.
     *
     * @param fileStream File stream to store.
     * @param resourceId Resource ID to store the file as.
     * @return resource ID.
     */
    @Override
    public UUID store(InputStream fileStream, UUID resourceId) {
        try {
            // Get the destination file path (storage location + relative path)
            Path destinationFile = this.fsConfiguration.getUploadDirPath().resolve(
                            Paths.get(resourceId.toString()))
                    .normalize().toAbsolutePath();

            // Save the file
            Files.copy(fileStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.trace("Resource {} stored at {}", resourceId, destinationFile);

            return resourceId;
        } catch (IOException e) {
            log.error("Could not store file", e);
            throw new StorageException("Could not store file", e);
        }
    }

    /**
     * Get a stream of all stored file paths.
     *
     * @return Stream of stored file paths.
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            // Recursively get all files in the upload directory
            return Files.walk(
                            this.fsConfiguration.getUploadDirPath(),
                            this.fsConfiguration.getMaxRecursionDepth()
                    )
                    .filter(path -> !path.equals(this.fsConfiguration.getUploadDirPath()))
                    .map(this.fsConfiguration.getUploadDirPath()::relativize);
        } catch (IOException e) {
            log.error("Could not load files", e);
            throw new StorageException("Could not load files", e);
        }
    }

    /**
     * Resolve a file path to a resource.
     *
     * @param resourceId resource ID.
     * @return resource file path.
     */
    public Path load(UUID resourceId) {
        return this.fsConfiguration.getUploadDirPath().resolve(resourceId.toString()).normalize();
    }

    /**
     * Load a file as a resource.
     *
     * @param resourceId ID of the resource to load.
     * @return loaded resource.
     */
    @Override
    public Resource loadAsResource(UUID resourceId) {
        try {
            // Get the resource path.
            Path file = load(resourceId);
            // Construct the resource.
            Resource resource = new UrlResource(file.toUri());
            // Verify that it exists and is readable.
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Could not read resource: " + resourceId);
            }
        } catch (MalformedURLException e) {
            log.warn("Could not resolve resource " + resourceId);
            throw new ResourceNotFoundException("Could not read file " + resourceId, e);
        }
    }

    /**
     * Delete all stored resources.
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.fsConfiguration.getUploadDirPath().toFile());
    }

    /**
     * Delete a resource.
     *
     * @param resourceId resource ID.
     * @return true if the resource was deleted, false otherwise.
     */
    @Override
    public boolean delete(UUID resourceId) {
        Path destinationFile = this.fsConfiguration.getUploadDirPath().resolve(
                        Paths.get(resourceId.toString()))
                .normalize().toAbsolutePath();
        if (Files.exists(destinationFile)) {
            try {
                Files.delete(destinationFile);
                log.debug("Resource {} deleted", resourceId);
                return true;
            } catch (IOException e) {
                log.warn("Could not delete resource {}", resourceId);
                log.warn("Error: {}", e.getMessage());
            }
        }
        return false;
    }

    /**
     * Get the URI of a resource.
     *
     * @param resourceId resource ID.
     * @return URI of the resource.
     */
    @Override
    public URI getURI(UUID resourceId) {
        return this.resourceConfiguration.getBaseUri().resolve(resourceId.toString()).normalize();
    }
}
