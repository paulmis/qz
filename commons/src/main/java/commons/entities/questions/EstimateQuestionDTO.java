package commons.entities.questions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import lombok.*;

/**
 * DTO for EstimateQuestions.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class EstimateQuestionDTO extends QuestionDTO {
    /**
     * Copy constructor.
     *
     * @param questionDTO the question to copy
     */
    public EstimateQuestionDTO(QuestionDTO questionDTO) {
        super(questionDTO);
    }
}
