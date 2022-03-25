package server.services.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.configuration.FileStorageConfiguration;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileStorageServiceTest {
    @Autowired
    private StorageService storageService;

    @Autowired
    private FileStorageConfiguration fileStorageConfiguration;

    private void writeToFile(String fileName, String content) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(content);
    }

    @Order(1)
    @Test
    void testInitialize() {
        storageService.init();
        // Verify that the upload directory exists
        assertTrue(Files.isDirectory(Path.of(fileStorageConfiguration.getUploadDir())));
    }

    @Order(2)
    @Test
    void testLoadAll() throws IOException {
        writeToFile(Paths.get(fileStorageConfiguration.getUploadDir(),
                "test.txt").toString(), "Hello World!");
        writeToFile(Paths.get(fileStorageConfiguration.getUploadDir(),
                "test2.txt").toString(), "Hello World!");
        // Verify that enough files are counted
        assertEquals(2, storageService.loadAll().count());
    }

    @Order(3)
    @Test
    void testLoad() {
        Path path = Paths.get(((FileStorageService) storageService).getFileStorageLocation().toString(),
                "test.txt");
        // Verify that the path is resolved properly
        assertEquals(path, storageService.load("test.txt"));
    }

    @Order(999)
    @Test
    void testDeleteAll() {
        storageService.deleteAll();
        // Verify that the upload directory does not exist
        assertTrue(Files.notExists(Path.of(fileStorageConfiguration.getUploadDir())));
    }
}