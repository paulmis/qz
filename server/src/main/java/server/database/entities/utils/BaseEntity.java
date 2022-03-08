package server.database.entities.utils;

import com.google.common.reflect.TypeToken;
import commons.entities.utils.DTO;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.GenericGenerator;
import org.modelmapper.ModelMapper;

/**
 * The base class for all database entities.
 *
 * @param <D> The DTO type of the entity.
 */
@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity<D extends DTO> {
    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    protected UUID id;

    /**
     * Convert the entity to a DTO.
     *
     * @return DTO for the specific entity.
     */
    public abstract D getDTO();
}
