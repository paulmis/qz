package commons.entities.questions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.ActivityDTO;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

/**
 * DTO for Questions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MCQuestionDTO.class, name = "MCQuestionDTO"),
    @JsonSubTypes.Type(value = EstimateQuestionDTO.class, name = "EstimateQuestionDTO")
})
@JsonView(Views.Public.class)
public class QuestionDTO implements DTO {
    /**
     * UUID of the question.
     */
    protected UUID id;

    /**
     * List of activities related to the question.
     */
    protected List<ActivityDTO> activities = new ArrayList<>();

    /**
     * Question text.
     */
    protected String text;

    /**
     * Icon corresponding to the question.
     * For guess consumption only.
     */
    protected String questionIcon = "";

    /**
     * Copy constructor.
     *
     * @param questionDTO the question DTO to copy
     */
    public QuestionDTO(QuestionDTO questionDTO) {
        this.id = questionDTO.id;
        this.activities = questionDTO.activities;
        this.text = questionDTO.text;
        this.questionIcon = questionDTO.questionIcon;
    }
}
