package server.database.entities.game;

import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.ActivityDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import commons.entities.game.GameDTO;
import commons.entities.game.ReactionDTO;
import commons.entities.utils.Views;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import server.database.entities.utils.BaseEntity;

/**
 * Reaction data structure - describes a single reaction.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Reaction extends BaseEntity<ReactionDTO> {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public Reaction(ReactionDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * The reaction type.
     */
    String id;


    @Override
    public ReactionDTO getDTO() {
        return new ModelMapper().map(this, ReactionDTO.class);
    }
}
