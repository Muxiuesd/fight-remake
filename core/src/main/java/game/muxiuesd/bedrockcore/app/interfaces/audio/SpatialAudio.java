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

    /**
     * 设置最小衰减距离，这个距离之内都是最大音量播放
     * */
    void setMinAttenuation (float minDistance);


    /**
     * 设置最大衰减距离，这个距离之外完全听不见
     * */
    void setMaxAttenuation (float maxDistance);

    /**
     * 设置衰减因子
     * */
    void setAttenuationFactor (float factor);

    /**
     * 启用或者禁用距离衰减
     * */
    void setAttenuationEnabled(boolean enabled);


    /**
     * 绑定空间音效发声源
     * */
    void setBoundSource (SpatialAudioSource source);

    /**
     * 获取空间音效发声源
     * */
    SpatialAudioSource getBoundSource ();

    /**
     * 解绑空间音效发声源
     * */
    void removeSpatialSource ();

    /**
     * 每帧由引擎调用，自动从绑定的源拉取位置
     * */
    void updatePos ();
}
