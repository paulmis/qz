package client.utils;

import java.util.Objects;

/**
 * Utilities for managing files.
 */
public class FileUtils {
    public static String defaultUserPic = Objects
            .requireNonNull(FileUtils.class.getResource("/client/images/logo.png"))
            .toExternalForm();
}
