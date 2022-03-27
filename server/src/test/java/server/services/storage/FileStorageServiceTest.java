package server.services.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static server.utils.TestHelpers.getUUID;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import server.configuration.FileStorageConfiguration;
import server.configuration.ResourceConfiguration;
import server.exceptions.ResourceNotFoundException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {
    @Mock
    private FileStorageConfiguration fileStorageConfiguration;

    @Mock
    private ResourceConfiguration resourceConfiguration;

    @InjectMocks
    private FileStorageService fileStorageService;

    private void writeToFile(String fileName, String content) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(content);
    }

    private InputStream stringToInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }

    @BeforeEach
    void setUp() {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "upload-dir");
        lenient().when(fileStorageConfiguration.getUploadDir())
                .thenReturn(tmpDir.toString());
        lenient().when(fileStorageConfiguration.getUploadDirPath())
                .thenReturn(tmpDir);
        lenient().when(fileStorageConfiguration.getMaxRecursionDepth()).thenReturn(2);

        lenient().when(resourceConfiguration.getBaseUri())
                .thenReturn(URI.create("http://localhost:8080/api/resource/"));

        fileStorageService.init();
    }

    @AfterEach
    void tearDown() {
        fileStorageService.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        // Delete the upload directory
        File uploadDir = new File(Paths.get(System.getProperty("java.io.tmpdir"), "upload-dir").toString());
        if (uploadDir.exists()) {
            uploadDir.delete();
        }
    }

    @Test
    void initialize() {
        // Verify that the upload directory exists
        assertTrue(Files.isDirectory(Path.of(fileStorageConfiguration.getUploadDir())));
    }

    @Test
    void loadAll() throws IOException {
        writeToFile(Paths.get(fileStorageConfiguration.getUploadDir(),
                getUUID(1).toString()).toString(), "Hello World!");
        writeToFile(Paths.get(fileStorageConfiguration.getUploadDir(),
                getUUID(2).toString()).toString(), "Hello World!");
        // Verify that enough files are counted
        assertEquals(2, fileStorageService.loadAll().count());
    }

    @Test
    void load() {
        Path path = Paths.get(fileStorageConfiguration.getUploadDir(),
                getUUID(1).toString());
        // Verify that the path is resolved properly
        assertEquals(path, fileStorageService.load(getUUID(1)));
    }

    @Test
    void loadAsResource() throws IOException {
        fileStorageService.store(stringToInputStream("123456789"), getUUID(1));

        // Load the saved resource
        Resource res = fileStorageService.loadAsResource(getUUID(1));
        // Verify that the resource exists
        assertTrue(fileStorageService.loadAsResource(getUUID(1)).exists());
        // Verify that it is readable
        assertTrue(res.isReadable());
        // Verify that the content is correct
        assertEquals(9, res.contentLength());
    }

    @Test
    void loadAsResourceNotFound() {
        fileStorageService.store(stringToInputStream("123456789"), getUUID(1));

        // Verify that the resource does not exist
        assertThrows(ResourceNotFoundException.class, () -> fileStorageService.loadAsResource(getUUID(2)).exists());
    }

    @Test
    void store() {
        fileStorageService.store(stringToInputStream("Hello, world!"), getUUID(1));
        // Verify that the file exists
        assertTrue(Files.exists(Path.of(fileStorageConfiguration.getUploadDir(),
                getUUID(1).toString())));
    }

    @Test
    void store2() {
        fileStorageService.store(stringToInputStream("Hello, world!"));
        // Verify that one file exists
        assertEquals(1, fileStorageService.loadAll().count());
    }

    @Test
    void getURI() {
        fileStorageService.store(stringToInputStream("Hello, world!"), getUUID(1));
        // Verify that the URI is correct
        assertEquals(URI.create("http://localhost:8080/api/resource/").resolve(getUUID(1).toString()),
                fileStorageService.getURI(getUUID(1)));
    }

    @Test
    void deleteAll() {
        fileStorageService.deleteAll();
        // Verify that the upload directory does not exist
        assertTrue(Files.notExists(Path.of(fileStorageConfiguration.getUploadDir())));
    }
}