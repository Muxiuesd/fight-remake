package ttk.muxiuesd.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 全局声音管理
 * <p>
 * sound和music统称为声音
 * <p>
 * TODO Mod实现添加音效（乐）和播放音效（乐）
 * */
public class AudioManager {
    public static String TAG = AudioManager.class.getName();

    private final String root = "audio/";

    private final LinkedHashMap<String, Sound> sounds = new LinkedHashMap<>();
    private final LinkedHashMap<String, Music> musics = new LinkedHashMap<>();

    private static final class Holder {
        private static final AudioManager INSTANCE = new AudioManager();
    }

    public static AudioManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 加载音效
     * @param id    音效的id
     * @param path  音效在audio文件夹里的路径
     * */
    public void loadSound(String id, String path) {
        if (getInstance().getSounds().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音效已经存在！！！");
            return;
        }
        AssetsLoader loader = AssetsLoader.getInstance();
        String soundPath = this.getPath(path);
        Class<Sound> soundClass = Sound.class;
        loader.loadAsync(soundPath, soundClass, () -> {
            Sound sound = loader.get(soundPath, soundClass);
            getInstance().getSounds().put(id, sound);
        });
    }

    /**
     * 加载音乐
     * @param id    音乐的id
     * @param path  音乐在audio文件夹里的路径
     * */
    public void loadMusic(String id, String path) {
        if (getInstance().getMusics().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音乐已经存在！！！");
            return;
        }
        AssetsLoader loader = AssetsLoader.getInstance();
        String musicPath = this.getPath(path);
        Class<Music> musicClass = Music.class;
        loader.loadAsync(musicPath, musicClass, () -> {
            Music music = loader.get(musicPath, musicClass);
            getInstance().getMusics().put(id, music);
        });
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
        if (!this.getSounds().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音效不存在，无法播放！！！");
            return null;
        }
        Sound sound = this.getSounds().get(id);
        sound.play(volume, pitch, pan);
        return sound;
    }

    public void playMusic(String id) {
        if (!this.getMusics().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音乐不存在，无法播放！！！");
            return;
        }
        Music music = this.getMusics().get(id);
        music.play();
    }

    public LinkedHashMap<String, Sound> getSounds() {
        return this.sounds;
    }

    public LinkedHashMap<String, Music> getMusics() {
        return this.musics;
    }

    public Sound getSound(String id) {
        return this.getSounds().get(id);
    }

    public Music getMusic(String id) {
        return this.getMusics().get(id);
    }

    /**
     * audio里面的路径
     * */
    private FileHandle getFile (String path) {
        return Gdx.files.internal(this.getPath(path));
    }

    public String getPath (String path) {
        return this.root + path;
    }
}
