package server.services.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import server.configuration.FileStorageConfiguration;
import server.exceptions.StorageException;
import server.exceptions.StorageNotFoundException;

/**
 * Service for filesystem storage.
 */
@Slf4j
@Service
public class FileStorageService implements StorageService {
    @Getter
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfiguration fileStorageConfiguration) {
        this.fileStorageLocation = Paths.get(fileStorageConfiguration.getUploadDir()).toAbsolutePath().normalize();
    }

    /**
     * Initialize the storage service.
     */
    @Override
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage", e);
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Store a file in the storage service.
     *
     * @param file File to store.
     */
    @Override
    public void store(MultipartFile file) {
        try {
            // Verify that the file is not empty
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }

            // Get the destination file path (storage location + relative path)
            Path destinationFile = this.fileStorageLocation.resolve(
                    Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            // Verify that the file is being uploaded to upload directory
            if (!destinationFile.startsWith(this.fileStorageLocation)) {
                log.warn("Potential directory traversal attack detected.");
                throw new StorageException("Could not resolve path " + file.getOriginalFilename());
            }
            // Save the file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
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
            return Files.walk(this.fileStorageLocation, 5)
                    .filter(path -> !path.equals(this.fileStorageLocation))
                    .map(this.fileStorageLocation::relativize);
        } catch (IOException e) {
            log.error("Could not load files", e);
            throw new StorageException("Could not load files", e);
        }
    }

    /**
     * Resolve a file path to a resource.
     *
     * @param filename file path.
     * @return resource file path.
     */
    @Override
    public Path load(String filename) {
        Path path = this.fileStorageLocation.resolve(filename).normalize();
        // Verify that the path is located within the upload directory
        if (path.startsWith(this.fileStorageLocation)) {
            return path;
        } else {
            log.warn("Potential directory traversal attack detected.");
            throw new StorageException("Could not resolve path " + filename);
        }
    }

    /**
     * Load a file as a resource.
     *
     * @param filename file path.
     * @return loaded resource.
     */
    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            log.warn("Could not resolve path " + filename);
            throw new StorageNotFoundException("Could not read file " + filename, e);
        }
    }

    /**
     * Delete all stored files.
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.fileStorageLocation.toFile());
    }
}
