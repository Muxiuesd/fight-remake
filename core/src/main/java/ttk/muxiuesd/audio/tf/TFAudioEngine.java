package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import de.pottgames.tuningfork.*;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioEngine;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * TuningFork实现的游戏立体音效引擎
 */
public class TFAudioEngine implements SpatialAudioEngine {
    public static final int SIMULTANEOUS_SOURCES = 500;
    public static final int IDLE_TASKS = 100;

    /// 单例模式
    private static final TFAudioEngine INSTANCE = new TFAudioEngine();
    public static TFAudioEngine getInstance () {
        return INSTANCE;
    }
    private TFAudioEngine() {}

    private Audio audio;
    private SpatialAudioListener audioListener;
    private Set<SpatialAudio> activeAudios;


    @Override
    public void init () {
        AudioDeviceConfig deviceConfig = new AudioDeviceConfig();
        deviceConfig.setOutputMode(OutputMode.STEREO_HRTF); //设置HRTF

        AudioConfig config = new AudioConfig(deviceConfig)
            .setSimultaneousSources(SIMULTANEOUS_SOURCES)
            .setIdleTasks(IDLE_TASKS);

        this.audio = Audio.init(config);

        this.audioListener = new TFAudioListener(this.audio.getListener());
        this.activeAudios = new HashSet<>();
    }

    /**
     * 通过文件路径加载音频文件，并且形成TuningFork的音频类来返回值
     * */
    @Override
    public SpatialAudio createAudio (FileHandle fileHandle) {
        //TODO 先这么写着
        SoundBuffer soundBuffer = SoundLoader.load(fileHandle);
        TFAudio audio = new TFAudio(this, this.audio.obtainSource(soundBuffer));
        ///如果音频没有播放就加入了活跃音频集合，就会在更新循环里从集合中删掉
        return audio;
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
        ArrayList<SpatialAudio> needRemovedAudios = new ArrayList<>();
        this.activeAudios.forEach((audio) -> {
            if (!audio.isPlaying()) {
                needRemovedAudios.add(audio);
            }
        });
        //移除不再播放的音频
        needRemovedAudios.forEach(this.activeAudios::remove);
        needRemovedAudios.forEach(Disposable::dispose);
        needRemovedAudios.clear();

        //对每一个活跃的空间音频进行坐标更新
        this.activeAudios.forEach(SpatialAudio::updatePos);
    }

    /**
     * 添加活跃的音频
     * */
    public void addActive (SpatialAudio audio) {
        this.activeAudios.add(audio);
    }


    @Override
    public void dispose () {
        this.audio.dispose();
    }

    public Audio getAudio () {
        return this.audio;
    }

    public SpatialAudioListener getAudioListener () {
        return this.audioListener;
    }
}
