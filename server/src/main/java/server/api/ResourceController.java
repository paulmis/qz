package server.api;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.io.IOException;
import java.net.URLConnection;
import java.util.UUID;
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
@RequestMapping("/api/resource")
@Controller
public class ResourceController {
    @Autowired
    private StorageService storageService;

    /**
     * Access a static resource.
     *
     * @param id the UUID of the resource to be accessed.
     * @return the resource.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serveResource(@PathVariable UUID id) throws IOException {
        Resource file = storageService.loadAsResource(id);
        return ResponseEntity.ok()
                // TODO: URLConnection.guessContentTypeFromStream is *slow*
                // Perhaps we should store MIME type as metadata somewhere? (in the database?)
                .contentType(MediaType.valueOf(firstNonNull(
                        URLConnection.guessContentTypeFromStream(file.getInputStream()),
                        MediaType.APPLICATION_OCTET_STREAM_VALUE)))
                .header("Content-Disposition",
                        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
