package commons.entities.questions;

/**
 * The type of multiple-choice question.
 */
public enum MCType {
    /**
     * Given an activity, guess its cost.
     */
    GUESS_COST,
    /**
     * Given a cost, guess the corresponding activity.
     */
    GUESS_ACTIVITY,
    /**
     * Given an activity, guess the activity with similar cost.
     */
    INSTEAD_OF,
}
