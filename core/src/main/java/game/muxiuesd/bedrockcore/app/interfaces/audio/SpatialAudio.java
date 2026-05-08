package game.muxiuesd.bedrockcore.app.interfaces.audio;

import com.badlogic.gdx.utils.Disposable;

/**
 * 空间音频接口
 * */
public interface SpatialAudio extends Disposable {
    void play ();
    void pause ();
    void resume ();
    void stop ();
    boolean isPlaying ();
    void setLooping (boolean looping);
    void setVolume (float volume); // 0.0 ~ 1.0
    void setPitch (float pitch);

    // 手动设置位置（设置后会自动解除 SpatialAudioSource 绑定）
    void setPos (float x, float y, float z);
    void setDirection (float x, float y, float z);
    void setAttenuation (float maxDistance);

    /*特效（示例使用预设 ID）*//*
    void applyReverb (int presetID);
    void removeReverb ();*/

    /**
     * 绑定
     * */
    void setSpatialSource (SpatialAudioSource source);
    /**
     * 解绑空间源
     * */
    void removeSpatialSource ();

    /**
     * 每帧由引擎调用，自动从绑定的源拉取位置
     * */
    void updatePos ();
}
