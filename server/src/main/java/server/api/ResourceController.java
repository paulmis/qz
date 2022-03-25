package server.api;

import java.net.URLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import server.services.storage.StorageService;

/**
 * Controller for accessing static files.
 */
@RequestMapping("/api/resources")
@Controller
public class ResourceController {
    @Autowired
    private StorageService storageService;

    /**
     * Access a static file.
     *
     * @param filename the name of the file to be accessed.
     * @return the file as a resource.
     */
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(URLConnection.guessContentTypeFromName(file.getFilename())))
                .header("Content-Disposition",
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
