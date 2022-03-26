package server.database.entities.answer;

import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import commons.entities.AnswerDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import server.database.entities.game.GamePlayer;
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
        this.id = getUUID(0);
        this.response = dto.getResponse();
    }

    /**
     * The list of activity costs from the Question given as an answer.
     */
    @ElementCollection
    @CollectionTable(name = "answer_responses", joinColumns = @JoinColumn(name = "answer_id"))
    @OrderColumn(name = "response_idx")
    protected List<Long> response = new ArrayList<>();

    /**
     * Timestamp of answer creation.
     */
    @Column(columnDefinition = "TIMESTAMP")
    protected LocalDateTime answerDate;

    /**
     * Automatically sets the creation date to when the entity is first persisted.
     */
    @Generated
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        answerDate = LocalDateTime.now();
    }

    /**
     * The player that gave the answer.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    protected GamePlayer player;

    /**
     * The set of answers to the same question this one belongs to.
     */
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected AnswerCollection answerCollection;

    @Override
    public AnswerDTO getDTO() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Answer, AnswerDTO> propertyMapper = modelMapper.createTypeMap(Answer.class, AnswerDTO.class);

        // Retrieval of question
        propertyMapper.addMappings(
                mapper -> mapper.map(ans -> {
                    if (answerCollection != null && answerCollection.getId() != null) {
                        return answerCollection.getId().getQuestionId();
                    }
                    return null;
                }, AnswerDTO::setQuestionId)
        );

        return modelMapper.map(this, AnswerDTO.class);
    }
}
