package client.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * The tests for the algorithmic utils class.
 */
class AlgorithmicUtilsTest {

    @Test
    void levenshteinDistanceInsertion() {
        assertEquals(1, AlgorithmicUtils.levenshteinDistance("aa", "aab"));
    }

    @Test
    void levenshteinDistanceDeletion() {
        assertEquals(1, AlgorithmicUtils.levenshteinDistance("aa", "a"));
    }

    @Test
    void levenshteinDistanceSubstitution() {
        assertEquals(1, AlgorithmicUtils.levenshteinDistance("aa", "ab"));
    }

    @Test
    void levenshteinDistanceBig() {
        assertEquals(8, AlgorithmicUtils.levenshteinDistance(
                "abc*efghijklm---nopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM+++NOPQRSTU*WXYZ"
        ));
    }
}