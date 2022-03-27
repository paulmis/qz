package server.configuration;

import java.net.URI;
import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Generated
@Data
@ConfigurationProperties(prefix = "server.resource")
public class ResourceConfiguration {
    private URI baseUri = URI.create("http://localhost:8080/api/resource/");
}
