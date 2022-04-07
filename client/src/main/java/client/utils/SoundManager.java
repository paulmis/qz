package client.utils;

import java.util.Objects;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * The sound manager. Manages all sound effects in the app.
 */
public class SoundManager {
    public static SimpleIntegerProperty volume = new SimpleIntegerProperty(100);
    public static SimpleBooleanProperty everyoneMuted = new SimpleBooleanProperty(false);

    /**
     * Plays a sound effect.
     *
     * @param soundEffect the sound effect enum requested.
     * @param playingSource the javafx class for the source of the image.
     */
    public static void playMusic(SoundEffect soundEffect, Class playingSource) {
        MediaPlayer player = new MediaPlayer(new Media(Objects.requireNonNull(playingSource
                .getResource("/client/sounds/" + soundEffect.name() + ".wav").toExternalForm())));
        player.setVolume(volume.get() / 100f);
        new Thread(player::play).start();
    }
}
