package server.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;


class MathHelpersTest {

    @Test
    void calculatePercentage100() {
        assertThat(1d, closeTo(MathHelpers.calculatePercentage(100, 100), 1e-2));
    }

    @Test
    void calculatePercentage50() {
        assertThat(0.5, closeTo(MathHelpers.calculatePercentage(74, 100), 1e-2));
    }

    @Test
    void calculatePercentage0() {
        assertThat(0d, closeTo(MathHelpers.calculatePercentage(-100, 100), 1e-2));
    }

    @Test
    void calculatePercentageIncreasing() {
        var scores = IntStream.range(0, 100)
                .mapToObj(i -> MathHelpers.calculatePercentage(i, 100))
                .collect(Collectors.toList());

        var sortedScores = scores.stream().sorted().collect(Collectors.toList());

        assertEquals(sortedScores, scores);
    }

    @Test
    void calculatePercentageDecreasing() {
        var scores = IntStream.range(100, 200)
                .mapToObj(i -> MathHelpers.calculatePercentage(i, 100))
                .collect(Collectors.toList());

        var sortedScores = scores.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

        assertEquals(sortedScores, scores);
    }

    @Test
    void erf0std() {
        assertThat(0d, closeTo(MathHelpers.erf(0), 1e-2));
    }

    @Test
    void erf1std() {
        assertThat(0.68, closeTo(MathHelpers.erf(1 / Math.sqrt(2)), 1e-2));
    }

    @Test
    void erf2std() {
        assertThat(0.95, closeTo(MathHelpers.erf(2 / Math.sqrt(2)), 1e-2));
    }

    @Test
    void erf3std() {
        assertThat(0.99, closeTo(MathHelpers.erf(3 / Math.sqrt(2)), 1e-2));
    }
}