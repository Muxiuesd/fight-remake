package game.muxiuesd.bedrockcore.app.interfaces.audio;

/**
 * 空间音效引擎
 * */
public interface SpatialAudioEngine {
    void init ();

    /**
     * 创建一个空间化音频源（不区分短音效与音乐）
     * */
    SpatialAudio createSpatialAudio (String filePath, boolean looping);

    SpatialAudioListener getListener ();

    void setMasterVolume (float volume);

    /**
     * 暂停所有音频
     * */
    void pauseAll ();

    /**
     * 恢复所有音频
     * */
    void resumeAll ();

    /**
     * 每帧调用，用于更新所有活跃声音的位置等信息
     * */
    void update (float delta);

    void dispose ();
}
