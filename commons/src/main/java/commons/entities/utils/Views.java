package commons.entities.utils;

/**
 * Tiers for data viewing - what data is to be sent to the client.
 * Used in @JsonView annotations.
 */
public class Views {
    /**
     * Data can be viewed by anyone.
     */
    public static class Public {}

    /**
     * Data can be viewed only by some users.
     * Example 1: only by admins.
     * Example 2: user's email can be viewed by the user himself, but not by other users.
     */
    public static class Private extends Public {}

    /**
     * Data is only for internal use.
     */
    public static class Internal extends Private {}
}
