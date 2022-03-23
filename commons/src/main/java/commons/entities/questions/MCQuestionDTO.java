package commons.entities.questions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * DTO for MCQuestions.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class MCQuestionDTO extends QuestionDTO {
    /**
     * Copy constructor.
     *
     * @param questionDTO the question to copy
     */
    public MCQuestionDTO(MCQuestionDTO questionDTO) {
        super(questionDTO);
    }
}
