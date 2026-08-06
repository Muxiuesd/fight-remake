package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.files.FileHandle;
import de.pottgames.tuningfork.*;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioEngine;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;
import game.muxiuesd.bedrockcore.util.Log;

import java.util.*;

/**
 * TuningFork实现的游戏立体音效引擎
 */
public class TFAudioEngine implements SpatialAudioEngine {
    public final String TAG = this.getClass().getName();
    public static final int SIMULTANEOUS_SOURCES = 1024;
    public static final int IDLE_TASKS = 100;
    // SoundBuffer 缓存上限（防止大量不同音频文件导致 AL buffer 无限增长）
    public static final int MAX_SOUND_BUFFERS = 1024;

    /// 单例模式
    private static final TFAudioEngine INSTANCE = new TFAudioEngine();
    public static TFAudioEngine getInstance () {
        return INSTANCE;
    }
    private TFAudioEngine() {}

    private Audio audio;
    private SpatialAudioListener audioListener;
    private Map<FileHandle, SoundBuffer> soundBuffersCache;     //音频文件数据的缓存
    private Set<SpatialAudio> activeAudios;

    //诊断统计（用于定位大量播放后无声的根因）
    private int sourcePoolExhaustedCount;
    private int soundLoadFailCount;
    private int activeAudiosPeak;
    private float statusLogTimer;

    //TODO 缓存SoundBuffer，减少AL开销，避免报错

    @Override
    public void init () {
        AudioDeviceConfig deviceConfig = new AudioDeviceConfig();
        deviceConfig.setOutputMode(OutputMode.STEREO_HRTF); //设置HRTF

        AudioConfig config = new AudioConfig(deviceConfig)
            .setSimultaneousSources(SIMULTANEOUS_SOURCES)
            .setIdleTasks(IDLE_TASKS)
            //禁用虚拟化：Virtualization.ON 会在同时播放的音频超过硬件 source 上限时
            //把音频"虚拟化"（不实际播放、静音，但状态保持播放中）——这会导致大量播放后部分音频无声
            .setVirtualization(AudioConfig.Virtualization.OFF_DROP_CHANNELS);

        this.audio = Audio.init(config);

        this.audioListener = new TFAudioListener(this.audio.getListener());
        this.soundBuffersCache = new HashMap<>();
        this.activeAudios = new HashSet<>();
    }

    /**
     * 通过文件路径加载音频文件，并且形成TuningFork的音频类来返回值
     * <p>
     * 播放前会先回收已停止的音频（释放 source），保证音频源池尽量可用，避免大量播放后无声
     * */
    @Override
    public SpatialAudio createAudio (FileHandle fileHandle) {
        Map<FileHandle, SoundBuffer> cache = this.getSoundBuffersCache();
        //一个buffer就是一个音频的内存数据，需要控制数量，可重复使用
        SoundBuffer soundBuffer;
        //先查找有无缓存
        if (cache.containsKey(fileHandle)) {
            soundBuffer = cache.get(fileHandle);
        }else {
            //缓存超限时清空（正常游戏音效数量有限，此处为防御动态加载/大量文件的情况）
            if (cache.size() >= MAX_SOUND_BUFFERS) {
                cache.clear();
            }
            //没有就加载，并放进缓存
            try {
                soundBuffer = SoundLoader.load(fileHandle);
            } catch (Exception e) {
                //音频文件加载失败（文件损坏/AL 资源不足）：记录日志并返回静默音频，不中断其他播放
                this.soundLoadFailCount++;
                Log.error(TAG, "音频文件加载失败：" + fileHandle.path() + "，" + e.getMessage());
                return new TFAudio(this, null);
            }
            cache.put(fileHandle, soundBuffer);
        }

        //播放前主动回收已停止的音频（释放其 source 回池），
        //避免同帧大量播放时音频源池被"已停止但尚未被 update 释放"的 source 占满导致新播放无声
        this.recycleStoppedAudios();

        //一个source就是一个播放实例，数量可以很多
        //TuningFork 在 source 池耗尽时 obtainSource 返回 null（见 SoundSourcePool.findFreeSource）
        BufferedSoundSource source = this.audio.obtainSource(soundBuffer);
        if (source == null) {
            //同一时刻播放的音效数量超过物理上限（极端情况），静默跳过本次播放，不崩溃
            this.sourcePoolExhaustedCount++;
            Log.error(TAG, "TuningFork 音频源池已耗尽（同一时刻播放的音效过多），本次播放被跳过");
            return new TFAudio(this, null);
        }
        return new TFAudio(this, source);
    }

    /**
     * 回收所有已停止播放的音频（释放其 source），使其可以被 TuningFork 复用
     */
    private void recycleStoppedAudios () {
        if (this.activeAudios.isEmpty()) return;
        ArrayList<SpatialAudio> stopped = new ArrayList<>();
        this.activeAudios.forEach((audio) -> {
            if (!audio.isPlaying()) stopped.add(audio);
        });
        if (stopped.isEmpty()) return;
        stopped.forEach(this.activeAudios::remove);
        for (SpatialAudio audio : stopped) {
            try {
                audio.dispose();
            } catch (Exception e) {
                //单个音频释放异常（如 TuningFork 内部已回收 source）不能中断整个回收流程
                Log.error(TAG, "回收音频失败：" + e.getMessage());
            }
        }
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
        for (SpatialAudio audio : needRemovedAudios) {
            try {
                audio.dispose();
            } catch (Exception e) {
                //单个音频释放异常（如 TuningFork 内部已回收 source）不能中断整个更新循环，否则后续音频永不清理
                Log.error(TAG, "释放音频失败：" + e.getMessage());
            }
        }
        needRemovedAudios.clear();

        //对每一个活跃的空间音频进行坐标更新
        for (SpatialAudio audio : this.activeAudios) {
            try {
                audio.updatePos();
            } catch (Exception e) {
                //单个音频位置更新异常不能中断整个更新循环
                Log.error(TAG, "更新音频位置失败：" + e.getMessage());
            }
        }

        //周期输出音频状态（诊断用）：活跃数/峰值/源池耗尽次数/加载失败次数
        this.activeAudiosPeak = Math.max(this.activeAudiosPeak, this.activeAudios.size());
        this.statusLogTimer += delta;
        if (this.statusLogTimer >= 5f) {
            this.statusLogTimer = 0f;
            Log.print(TAG, "音频状态：活跃=" + this.activeAudios.size()
                + " 峰值=" + this.activeAudiosPeak
                + " 源池耗尽=" + this.sourcePoolExhaustedCount
                + " 加载失败=" + this.soundLoadFailCount);
        }
    }

    /**
     * 添加活跃的音频
     * */
    public void addActive (SpatialAudio audio) {
        this.activeAudios.add(audio);
    }


    @Override
    public void dispose () {
        this.activeAudios.forEach(SpatialAudio::dispose);
        this.activeAudios.clear();
        this.soundBuffersCache.values().forEach(SoundBuffer::dispose);
        this.soundBuffersCache.clear();
        this.audio.dispose();
    }

    public Audio getAudio () {
        return this.audio;
    }

    public SpatialAudioListener getAudioListener () {
        return this.audioListener;
    }

    public Map<FileHandle, SoundBuffer> getSoundBuffersCache () {
        return this.soundBuffersCache;
    }

    public TFAudioEngine setSoundBuffersCache (Map<FileHandle, SoundBuffer> soundBuffersCache) {
        this.soundBuffersCache = soundBuffersCache;
        return this;
    }
}
