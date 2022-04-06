package client.utils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class SoundManager {
    public static SimpleIntegerProperty volume = new SimpleIntegerProperty(100);

    public static void PlayMusic(SoundEffect soundEffect, Class playingSource) {
        MediaPlayer player = new MediaPlayer(new Media(Objects.requireNonNull(playingSource
                .getResource("/client/sounds/" + soundEffect.name() + ".wav").toExternalForm())));
        player.setVolume(volume.get() / 100f);
        new Thread(player::play).start();
    }
}
