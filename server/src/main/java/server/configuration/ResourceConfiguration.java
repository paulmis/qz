package server.configuration;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration the resource server - where the resources can be accessed, etc.
 */
@Data
@ConfigurationProperties(prefix = "server.resource")
public class ResourceConfiguration {
    /**
     * The resource base URL - prepended to resource UUIDs to get the access URL.
     * This is the base URL of the resource server.
     */
    private URI baseUri = URI.create("http://localhost:8080/api/resource/");
}
