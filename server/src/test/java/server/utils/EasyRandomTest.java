package server.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EasyRandomTest {
    /**
     * Tests that the LCG is seeded properly when default constructor is used.
     */
    @Test
    void testSeedRandom() {
        EasyRandom generator1 = new EasyRandom();
        EasyRandom generator2 = new EasyRandom();

        assertNotEquals(generator1.getRandom(), generator2.getRandom());
    }

    /**
     * Test whether the random seed produces correct results.
     */
    @Test
    void testRepeatability() {
        EasyRandom generator = new EasyRandom(10);
        List<Integer> expected = List.of(-1849737093,
                1849040536,
                -1410496783,
                581309142,
                1106733399,
                146443332,
                1321378093,
                -339179934,
                1327449075,
                2012620976);

        List<Integer> generated = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Integer integer = generator.getRandom();
            generated.add(integer);
        }

        assertEquals(expected, generated);
    }

    /**
     * Test whether the generator respects the bounds.
     */
    @Test
    void testBounds() {
        EasyRandom generator = new EasyRandom();

        for (int i = 0; i < 100; i++) {
            Integer integer = generator.getRandom(10, 15);
            assertTrue(integer >= 10);
            assertTrue(integer < 15);
        }
    }

    /**
     * Test whether the bounded generation method produces correct results.
     */
    @Test
    void testBoundsRepeatability() {
        EasyRandom generator = new EasyRandom(1234);
        List<Integer> expected = List.of(13, 10, 11, 16, 13, 12, 15, 18, 15, 14);

        List<Integer> generated = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Integer integer = generator.getRandom(10, 20);
            generated.add(integer);
        }

        assertEquals(expected, generated);
    }
}