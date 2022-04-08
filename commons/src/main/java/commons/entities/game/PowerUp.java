package commons.entities.game;

import lombok.Getter;

/**
 * The power-up types that can be played.
 */
public enum PowerUp {
    /**
     * The halve time power-up.
     */
    HalveTime("half time power-up"),
    /**
     * The point doubling power-up.
     */
    DoublePoints("double points power-up"),
    /**
     * The power-up to eliminate an incorrect answer.
     */
    IncorrectAnswer("eliminate incorrect answer power-up");

    @Getter
    public final String powerUpName;

    PowerUp(String powerUpName) {
        this.powerUpName = powerUpName;
    }
}