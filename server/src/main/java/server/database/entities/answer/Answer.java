package server.database.entities.answer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import server.database.entities.game.GamePlayer;
import server.database.entities.question.Activity;
import server.database.entities.question.Question;
import server.database.entities.utils.BaseEntity;

/**
 * Answer entity - describes the answer given by a player to one of the questions of the quiz.
 */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@Entity
public class Answer extends BaseEntity<AnswerDTO> {

    /**
     * Construct a new answer from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public Answer(AnswerDTO dto) {
        this.id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.response = dto.getResponse().stream().map(Activity::new).collect(Collectors.toList());
    }

    /**
     * The list of activities from the Question given as an answer.
     */
    @ManyToMany
    protected List<Activity> response = new ArrayList<>();

    /**
     * The player that gave the answer.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private GamePlayer player;

    /**
     * The question that this answer refers to.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;

    /**
     * The set of answers to the same question this one belongs to.
     */
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_collection_id", nullable = false)
    private AnswerCollection answerCollection;

    /**
     * Convert user choices to DTO.
     *
     * @return a list of ActivityDTOs
     */
    protected List<ActivityDTO> getResponseDTO() {
        return this.response.stream().map(Activity::getDTO).collect(Collectors.toList());
    }

    @Override
    public AnswerDTO getDTO() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Answer, AnswerDTO> propertyMapper = modelMapper.createTypeMap(Answer.class, AnswerDTO.class);

        // Deep conversion of user choice
        propertyMapper.addMappings(
                mapper -> mapper.map(Answer::getResponseDTO, AnswerDTO::setResponse)
        );

        return modelMapper.map(this, AnswerDTO.class);
    }
}
