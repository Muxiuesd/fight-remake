package ttk.muxiuesd.system.game;

import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioEngine;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.tf.TFAudioEngine;
import ttk.muxiuesd.system.abs.GameSystem;

/**
 * 游戏底层的立体音频系统
 * */
public class SpatialAudioSystem extends GameSystem {
    /// 单例模式
    private static SpatialAudioSystem INSTANCE = new SpatialAudioSystem();
    public static SpatialAudioSystem getInstance () {
        return INSTANCE;
    }
    private SpatialAudioSystem() {}

    private SpatialAudioEngine audioEngine;     //游戏的立体音频引擎
    private SpatialAudioListener audioListener; //游戏音频的接听者


    @Override
    public void initialize () {
        //初始化具体的音频引擎的实现类，这里是使用的TuningFork
        this.audioEngine = TFAudioEngine.getInstance();

        //初始化
        this.audioEngine.init();
        this.audioEngine.setMasterVolume(1f);
        this.audioListener = this.audioEngine.getListener();

        Log.print(TAG(), "立体音频系统初始化完成");
    }

    @Override
    public void update (float delta) {
        this.audioEngine.update(delta);
    }

    /**
     * 播放UI音频，也就是没有距离衰减的音频
     * */
    public SpatialAudio playUIAudio (AudioHolder audioHolder, SpatialAudioSource source) {
        SpatialAudio audio = this.playAudio(audioHolder, source);
        audio.setAttenuationEnabled(false);
        return audio;
    }

    /**
     * 播放音频
     * @param audioHolder 音频的注册类
     * @param source 音频的发声源
     * */
    public SpatialAudio playAudio (AudioHolder audioHolder, SpatialAudioSource source) {
        SpatialAudio audio = this.audioEngine.createAudio(audioHolder.getFileHandle());
        //绑定音频的发声源
        audio.setBoundSource(source);
        audio.play();

        return audio;
    }

    @Override
    public void dispose () {
        this.audioEngine.dispose();
    }

    public SpatialAudioEngine getAudioEngine () {
        return this.audioEngine;
    }

    public SpatialAudioSystem setAudioEngine (SpatialAudioEngine audioEngine) {
        this.audioEngine = audioEngine;
        return this;
    }

    public SpatialAudioListener getAudioListener () {
        return this.audioListener;
    }

    public SpatialAudioSystem setAudioListener (SpatialAudioListener audioListener) {
        this.audioListener = audioListener;
        return this;
    }
}
