package server.configuration;

import java.nio.file.Path;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Store the configuration of the file storage.
 */
@Generated
@Data
@ConfigurationProperties(prefix = "server.storage")
public class FileSystemStorageConfiguration {
    private String uploadDir = "uploads";

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
