package server.utils;

import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A simple LCG that generates numbers using glib rand() parameters.
 * See: https://en.wikipedia.org/wiki/Linear_congruential_generator
 */
@Embeddable
@AllArgsConstructor
public class EasyRandom {
    @Transient
    private final int modulo = 1 << 31;
    @Transient
    private final int multiplier = 1103515245;
    @Transient
    private final int increment = 12345;

    @NonNull private Integer random;

    /**
     * Instantiate a new EasyRandom object with a random seed.
     */
    public EasyRandom() {
        this.random = ThreadLocalRandom.current().nextInt();
    }

    /** Generates a new random number.
     *
     * @return a random number between 0 and 2^31.
     */
    public Integer getRandom() {
        this.random = (this.random * multiplier + increment) % modulo;
        return this.random;
    }

    /** Generates a random number between min and max.
     *
     * @param min the minimum value to generate (inclusive).
     * @param max the maximum value to generate (exclusive).
     * @return a random number between min and max.
     */
    public Integer getRandom(Integer min, Integer max) {
        Integer value = getRandom();
        // Java's modulo operator is not the same as the one in C.
        // See: https://stackoverflow.com/questions/5385024/mod-in-java-produces-negative-numbers
        return Math.floorMod(value, (max - min)) + min;
    }

    /** Set the internal state of the generator.
     *
     * @param newState the new random state to set.
     */
    public void setRandom(@NonNull Integer newState) {
        random = newState;
    }
}

