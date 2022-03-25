package server.configuration;

import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Store the configuration of the file storage.
 */
@Generated
@ConfigurationProperties(prefix = "storage")
public class FileStorageConfiguration {
    private String uploadDir = "uploads";

    /**
     * Get the upload directory.
     *
     * @return the directory to upload to.
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Set the upload directory.
     *
     * @param uploadDir the directory to upload to.
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
