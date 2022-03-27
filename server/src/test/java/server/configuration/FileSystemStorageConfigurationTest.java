package server.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FileSystemStorageConfigurationTest {

    @Test
    void getUploadDirPath() {
        FileSystemStorageConfiguration fileSystemStorageConfiguration = new FileSystemStorageConfiguration();

        // Test default value
        String uploadDir = System.getProperty("java.io.tmpdir");

        // Verify that the function parses the path properly.
        fileSystemStorageConfiguration.setUploadDir(uploadDir);
        assertEquals(Path.of(uploadDir), fileSystemStorageConfiguration.getUploadDirPath());
    }
}