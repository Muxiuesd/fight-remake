package ttk.muxiuesd.system.game;

import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioEngine;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.audio.tf.TFAudioEngine;
import ttk.muxiuesd.system.abs.GameSystem;

/**
 * 游戏的立体音频系统
 * */
public class SpatialAudioSystem extends GameSystem {
    /// 单例模式
    private static SpatialAudioSystem INSTANCE = new SpatialAudioSystem();
    public static SpatialAudioSystem getInstance() {
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
        this.audioListener = this.audioEngine.getListener();
    }

    @Override
    public void update (float delta) {
        this.audioEngine.update(delta);
    }

    /**
     * 播放音效
     * @param audio 音频的注册类
     * @param source 音频的发声源
     * */
    public SpatialAudio playAudio (Audio audio, SpatialAudioSource source) {
        //通过id来获取音频文件路径
        String filePath = AssetsLoader.getInstance().getPath(audio.getID());
        SpatialAudio spatialAudio = this.audioEngine.createAudio(filePath);
        spatialAudio.setSpatialSource(source);
        spatialAudio.play();

        return spatialAudio;
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
