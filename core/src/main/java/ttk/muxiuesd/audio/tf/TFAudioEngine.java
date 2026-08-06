package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.files.FileHandle;
import de.pottgames.tuningfork.*;
import de.pottgames.tuningfork.logger.TuningForkLogger;
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
    //同时可用的音频源数量：不能超过硬件/驱动的 OpenAL source 上限（常见限制 256），
    //超出部分 alGenSources 会返回 0（无效 sourceId），播放失败 + free 报 AL_INVALID_NAME（大量播放后无声的根因）
    //游戏同时播放的音频（走路+攻击+环境+UI）一般 < 50，128 完全够用
    public static final int SIMULTANEOUS_SOURCES = 128;
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
    private final TuningForkErrorCatcher errorCatcher = new TuningForkErrorCatcher();

    /**
     * 自定义 TuningFork 日志捕获器：监听 TuningFork 内部的错误日志，
     * 特别是 SoundBuffer 创建失败（"Failed to create the SoundBuffer - AL_INVALID_NAME"）
     * 这类 AL 层面错误——出现时标记，后续清空缓存强制重新加载，避免无效 buffer 反复使用导致永久无声
     */
    private class TuningForkErrorCatcher implements TuningForkLogger {
        //volatile：TuningFork 可能在内部线程调用 logger
        private volatile boolean soundBufferError;

        @Override
        public void error (Class<?> clazz, String message) {
            Log.error(TAG, "[TuningFork] " + clazz.getSimpleName() + ": " + message);
            if (message != null
                && (message.contains("SoundBuffer")
                    || message.toLowerCase().contains("buffer")
                    || message.contains("AL_INVALID_NAME"))) {
                this.soundBufferError = true;
            }
        }

        @Override
        public void warn (Class<?> clazz, String message) {
        }

        @Override
        public void info (Class<?> clazz, String message) {
        }

        @Override
        public void debug (Class<?> clazz, String message) {
        }

        @Override
        public void trace (Class<?> clazz, String message) {
        }

        /**
         * 消费并清除 SoundBuffer 错误标记
         */
        boolean consumeSoundBufferError () {
            boolean flag = this.soundBufferError;
            this.soundBufferError = false;
            return flag;
        }
    }

    @Override
    public void init () {
        AudioDeviceConfig deviceConfig = new AudioDeviceConfig();
        deviceConfig.setOutputMode(OutputMode.STEREO_HRTF); //设置HRTF

        AudioConfig config = new AudioConfig(
            deviceConfig,
            DistanceAttenuationModel.INVERSE_DISTANCE_CLAMPED,
            SIMULTANEOUS_SOURCES,
            IDLE_TASKS,
            //禁用虚拟化：Virtualization.ON 会在同时播放的音频超过硬件 source 上限时
            //把音频"虚拟化"（不实际播放、静音，但状态保持播放中）——这会导致大量播放后部分音频无声
            AudioConfig.Virtualization.OFF_DROP_CHANNELS,
            this.errorCatcher
        );

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
        //TuningFork 报告过 SoundBuffer 错误：清空缓存强制重新加载（缓存中可能有无效 buffer）
        if (this.errorCatcher.consumeSoundBufferError()) {
            cache.clear();
        }

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
                Log.error(TAG, "音频文件加载失败：" + fileHandle.path() + "，" + e.getMessage());
                return new TFAudio(this, null);
            }
            //刚发生了 SoundBuffer 创建错误（如 AL_INVALID_NAME）：这个 buffer 无效，不缓存、不播放
            if (this.errorCatcher.consumeSoundBufferError()) {
                return new TFAudio(this, null);
            }
            cache.put(fileHandle, soundBuffer);
        }

        //播放前主动回收已停止的音频（释放其 source 回池），
        //避免同帧大量播放时音频源池被"已停止但尚未被 update 释放"的 source 占满导致新播放无声
        this.recycleStoppedAudios();

        //一个source就是一个播放实例，数量可以很多
        //TuningFork 在 source 池耗尽时 obtainSource 返回 null（同一时刻播放超过上限），此时静默跳过本次播放
        BufferedSoundSource source = this.audio.obtainSource(soundBuffer);
        if (source == null) {
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
