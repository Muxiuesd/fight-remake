package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.Gdx;
import de.pottgames.tuningfork.Audio;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.SoundBuffer;
import de.pottgames.tuningfork.SoundLoader;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioEngine;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;

/**
 * TuningFork实现的游戏立体音效引擎
 */
public class TFAudioEngine implements SpatialAudioEngine {
    public static final int SIMULTANEOUS_SOURCES = 200;
    public static final int IDLE_TASKS = 20;

    /// 单例模式
    private static final TFAudioEngine INSTANCE = new TFAudioEngine();
    public static TFAudioEngine getInstance () {
        return INSTANCE;
    }
    private TFAudioEngine() {}

    private Audio audio;
    private SpatialAudioListener audioListener;

    @Override
    public void init () {
        AudioConfig config = new AudioConfig()
            .setSimultaneousSources(SIMULTANEOUS_SOURCES)
            .setIdleTasks(IDLE_TASKS);

        this.audio = Audio.init(config);

        this.audioListener = new TFAudioListener(this.audio.getListener());
    }

    /**
     * 通过文件路径加载音频文件，并且形成TuningFork的音频类来返回值
     * */
    @Override
    public SpatialAudio createAudio (String filePath) {

        SoundBuffer soundBuffer = SoundLoader.load(Gdx.files.internal(filePath));
        TFAudio tfAudio = new TFAudio(this.audio.obtainSource(soundBuffer));
        return tfAudio;
    }

    @Override
    public SpatialAudioListener getListener () {
        return this.audioListener;
    }

    @Override
    public void setMasterVolume (float volume) {
        this.audio.setMasterVolume(volume);
    }

    @Override
    public void pauseAll () {
        this.audio.pauseAll();
    }

    @Override
    public void resumeAll () {
        this.audio.resumeAll();
    }

    @Override
    public void update (float delta) {

    }

    @Override
    public void dispose () {
        this.audio.dispose();
    }
}
