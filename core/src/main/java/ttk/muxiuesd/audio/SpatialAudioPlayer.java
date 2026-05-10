package ttk.muxiuesd.audio;

import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.system.game.SpatialAudioSystem;

/**
 * 空间音效播放器
 * */
public class SpatialAudioPlayer {

    public static SpatialAudio play (AudioHolder audioHolder, SpatialAudioSource source) {
        return SpatialAudioSystem.getInstance().playAudio(audioHolder, source);
    }
}
