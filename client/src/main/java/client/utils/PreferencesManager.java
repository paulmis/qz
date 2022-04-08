package client.utils;

import client.Main;
import java.util.prefs.Preferences;

/**
 * Manage user configuration.
 */
public class PreferencesManager {
    /**
     * The preferences.
     */
    public static final Preferences preferences = Preferences.userNodeForPackage(Main.class);
}
