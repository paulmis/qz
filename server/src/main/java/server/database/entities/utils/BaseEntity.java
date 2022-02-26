package server.database.entities.utils;

import com.google.common.reflect.TypeToken;
import commons.entities.utils.DTO;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 * The base class for all database entities.
 *
 * @param <D> The DTO type of the entity.
 */
@Data
@MappedSuperclass
public abstract class BaseEntity<D extends DTO> {
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
        TypeToken<D> typeToken = new TypeToken<D>(getClass()) {};
        Type type = typeToken.getType();
        return new ModelMapper().map(this, type);
    }
}
