package server.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaveableRandomTest {
    /**
     * Tests that the LCG is seeded properly when default constructor is used.
     */
    @Test
    void testSeedRandom() {
        SaveableRandom generator1 = new SaveableRandom();
        SaveableRandom generator2 = new SaveableRandom();

        assertNotEquals(generator1.getState(), generator2.getState());
    }

    /**
     * Test whether the random seed produces correct results.
     */
    @Test
    void testRepeatability() {
        SaveableRandom generator = new SaveableRandom(10);
        List<Integer> expected = List.of(3847489,
                1334288366,
                1486862010,
                711662464,
                -1453296530,
                -775316920,
                1157481928,
                294681619,
                -753148084,
                697431532);

        List<Integer> generated = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Integer integer = generator.nextInt();
            generated.add(integer);
        }

        assertEquals(expected, generated);
    }

    /**
     * Test whether the bounded generation method produces correct results.
     */
    @Test
    void testBoundsRepeatability() {
        SaveableRandom generator = new SaveableRandom(1234);
        List<Integer> expected = List.of(1, 8, 3, 5, 9, 7, 1, 8, 6, 6);

        List<Integer> generated = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Integer integer = generator.nextInt(10);
            generated.add(integer);
        }

        assertEquals(expected, generated);
    }
}