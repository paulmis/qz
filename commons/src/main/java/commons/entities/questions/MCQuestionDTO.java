package commons.entities.questions;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import lombok.*;

/**
 * DTO for MCQuestions.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class MCQuestionDTO extends QuestionDTO {

    /**
     * Boolean to indicate if the user has to guess the energy consumption
     * or the corresponding activity.
     */
    protected boolean guessConsumption = true;

    /**
     * Copy constructor.
     *
     * @param questionDTO the question to copy
     */
    public MCQuestionDTO(QuestionDTO questionDTO, boolean guessConsumption) {
        super(questionDTO);
        this.guessConsumption = guessConsumption;
    }
}
