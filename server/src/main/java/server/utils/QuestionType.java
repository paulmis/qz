package server.utils;

/**
 * Structure representing a question class and its subtypes.
 */

public class QuestionType {
    public Class questionType;
    public Enum questionSubtype;

    public QuestionType(Class type, Enum subtype) {
        this.questionType = type;
        this.questionSubtype = subtype;
    }

    public QuestionType(Class type) {
        this(type, null);
    }
}
