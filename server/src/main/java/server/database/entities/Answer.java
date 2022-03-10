package server.database.entities;

import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Activity;
import server.database.entities.utils.BaseEntity;

/**
 * Answer entity - describes the answer given by a player to one of the questions of the quiz.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Answer extends BaseEntity<AnswerDTO> {
    /**
     * Construct a new answer from a DTO.
     *
     * @param dto    DTO to map to entity.
     * @param player the player that gave the answer.
     */
    public Answer(AnswerDTO dto, GamePlayer player) {
        this.player = player;
        this.userChoice = dto.getUserChoice().stream().map(Activity::new).collect(Collectors.toList());
    }

    /**
     * The list of activities from the Question given as an answer.
     */
    @ManyToMany
    protected List<Activity> userChoice = new ArrayList<>();

    /**
     * The player that gave the answer.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private GamePlayer player;

    @Override
    public AnswerDTO getDTO() {
        return new ModelMapper().map(this, AnswerDTO.class);
    }
}
