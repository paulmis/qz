package server.utils;

/**
 * Provides helper methods for math functions.
 */
public class MathHelpers {

    /**
     * Calculates the percentage of items lower than the provided value
     * in a normal distribution centered around the parameter center.
     *
     * @param value the value that we want to find the percentage of.
     * @param center the center of the distribution.
     * @return the percentage.
     */
    public static double calculatePercentage(double value, double center) {
        var dist = Math.abs(value - center) * Math.abs(value - center);
        var std = dist / (Math.max(31, Math.pow(Math.sqrt(Math.abs(center)), 3)));
        var perc = Math.abs(erf(std / Math.sqrt(2)));
        return Math.max(0, 1 - perc);
    }

    /**
     * Calculates the gauss error function.
     *
     * @param z the value for which we want to calculate the error function.
     * @return the result of the computation.
     */
    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        double ans = 1 - t * Math.exp(-z * z - 1.26551223
                + t * (1.00002368
                + t * (0.37409196
                + t * (0.09678418
                + t * (-0.18628806
                + t * (0.27886807
                + t * (-1.13520398
                + t * (1.48851587
                + t * (-0.82215223
                + t * (0.17087277))))))))));

        return (z >= 0) ? ans : -ans;
    }
}
