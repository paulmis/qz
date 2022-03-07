package server.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * A simple LCG that generates numbers using Java random parameters.
 * See: https://en.wikipedia.org/wiki/Linear_congruential_generator
 */
@Embeddable
@AllArgsConstructor
public class SaveableRandom extends Random {
    @Transient
    private final long modulo = 1L << 48;
    @Transient
    private final long multiplier = 0x5DEECE66DL;
    @Transient
    private final long increment = 11L;

    /**
     * Internal state of the LCG.
     */
    @Getter
    private long state;

    /**
     * Instantiate a new EasyRandom object with a random seed.
     */
    public SaveableRandom() {
        this.state = ThreadLocalRandom.current().nextLong();
    }

    /**
     * Generates a new random number.
     *
     * @return a random number between 0 and 2^48.
     */
    @Override
    public int next(int bits) {
        this.state = (this.state * multiplier + increment) % modulo;
        return (int) (this.state >>> (48 - bits));
    }

    /**
     * Set the internal state of the generator.
     *
     * @param newState the new random state to set.
     */
    public void setSeed(@NonNull Long newState) {
        state = newState;
    }
}

