package server.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.Transient;
import lombok.*;

/**
 * A simple LCG that generates numbers using Java random parameters.
 * See: https://en.wikipedia.org/wiki/Linear_congruential_generator
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
     * @throws NullPointerException if the state is null.
     */
    @Override
    public int next(int bits) throws NullPointerException {
        this.state = (this.state * multiplier + increment) % modulo;
        return (int) (this.state >>> (48 - bits));
    }
}

