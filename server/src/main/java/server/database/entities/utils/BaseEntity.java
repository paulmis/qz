package server.database.entities.utils;

import com.google.common.reflect.TypeToken;
import commons.entities.utils.DTO;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.persistence.Id;
import org.modelmapper.ModelMapper;

/**
 * The base class for all database entities.
 *
 * @param <D> The DTO type of the entity.
 */
public abstract class BaseEntity<D extends DTO> {
    private final TypeToken<D> typeToken = new TypeToken<D>(getClass()) {};
    private final Type type = typeToken.getType();

    private final ModelMapper mapper = new ModelMapper();

    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Id
    protected UUID id;

    /**
     * Convert the entity to a DTO.
     *
     * @return DTO for the specific entity.
     */
    public D getDTO() {
        return this.mapper.map(this, type);
    }
}
