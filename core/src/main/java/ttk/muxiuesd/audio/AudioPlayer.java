package ttk.muxiuesd.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 声音播放器
 * <p>
 * 没有特殊音效方面的需求的话可以直接调用此类播放
 * */
public class AudioPlayer {
    public final String TAG = this.getClass().getName();

    private AudioPlayer() {}
    private static final class Holder {
        private static final AudioPlayer INSTANCE = new AudioPlayer();
    }
    public static AudioPlayer getInstance() {
        return AudioPlayer.Holder.INSTANCE;
    }

    public Sound playSound(String id) {
        return this.playSound(id, 1f);
    }
    public Sound playSound(String id, float volume) {
        return this.playSound(id, volume, 1f);
    }
    public Sound playSound(String id, float volume, float pitch) {
        return this.playSound(id, volume, pitch, 0f);
    }
    public Sound playSound(String id, float volume, float pitch, float pan) {
        LinkedHashMap<String, Sound> soundCache = AudioLoader.getInstance().getSoundCache();
        if (!soundCache.containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音效不存在，无法播放！！！");
            return null;
        }
        Sound sound = soundCache.get(id);
        sound.play(volume, pitch, pan);
        return sound;
    }

    /**
     * 播放音乐，若已经在播放则中途停下来再重头播放
     * */
    public void playMusic(String id) {
        LinkedHashMap<String, Music> musicCache = AudioLoader.getInstance().getMusicCache();
        if (!musicCache.containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音乐不存在，无法播放！！！");
            return;
        }
        Music music = musicCache.get(id);
        if (music.isPlaying()) {
            music.stop();
        }
        music.play();
    }
}
