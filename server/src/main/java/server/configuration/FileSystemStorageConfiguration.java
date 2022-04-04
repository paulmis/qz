package server.configuration;

import java.nio.file.Path;
import javax.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Store the configuration of the file storage.
 */
@Data
@ConfigurationProperties(prefix = "server.storage")
public class FileSystemStorageConfiguration {
    /**
     * The root path of the file storage.
     * Can be a relative or an absolute path.
     */
    private String uploadDir = "uploads";

    /**
     * When scanning the upload directory, ignore files that are nested deeper than this value.
     */
    @Min(1)
    private Integer maxRecursionDepth = 5;

    /**
     * Get the path of the upload directory.
     *
     * @return the path to the upload directory.
     */
    public Path getUploadDirPath() {
        return Path.of(uploadDir);
    }
}
