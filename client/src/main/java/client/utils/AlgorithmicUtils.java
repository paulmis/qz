package client.utils;

/**
 * Algorithmic method helpers.
 */
public class AlgorithmicUtils {

    /**
     * A function that calculates the levenshtein distance
     * between two strings.
     *
     * @param a The first string.
     * @param b The other string.
     * @return the levenshtein distance between the two strings.
     */
    public static int levenshteinDistance(String a, String b) {
        var dist = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dist[i][0] = i;
        }

        for (int i = 0; i <= b.length(); i++) {
            dist[0][i] = i;
        }

        for (int j = 1; j <= b.length(); j++) {
            for (int i = 1; i <= a.length(); i++) {
                dist[i][j] =
                        Math.min(
                                Math.min(
                                        dist[i - 1][j] + 1,
                                        dist[i][j - 1] + 1),
                                (dist[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1)));
            }
        }

        return dist[a.length()][b.length()];
    }
}
