package server;

import com.google.common.base.Strings;
import java.util.UUID;

/**
 * Common functionality used in testing.
 */
public class TestHelpers {

    /**
     * Generate a UUID from a string.
     *
     * @param value the string to generate the UUID from
     * @return a UUID generated from the string
     */
    public static UUID getUUID(String value) {
        if (value.length() > 12) {
            throw new IllegalArgumentException("The number (converted to decimal) can be at most 12 characters long");
        }
        return UUID.fromString(
                String.format("00000000-0000-0000-0000-%s",
                        Strings.padStart(value, 12, '0')));
    }

    /**
     * Generate a UUID from a positive integer.
     *
     * @param i the number to generate a UUID from.
     * @return a UUID generated from the number.
     */
    public static UUID getUUID(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("The number must be positive");
        }
        return getUUID(String.valueOf(i));
    }
}
