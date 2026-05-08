package ttk.muxiuesd.audio;

import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.system.game.SpatialAudioSystem;

/**
 * 空间音效播放器
 * */
public class SpatialAudioPlayer {

    public static SpatialAudio play (Audio audio, SpatialAudioSource source) {
        return SpatialAudioSystem.getInstance().playAudio(audio, source);
    }
}
