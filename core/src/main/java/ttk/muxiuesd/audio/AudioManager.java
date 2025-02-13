package ttk.muxiuesd.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 音效管理
 * TODO Mod实现添加音效和播放音效
 * */
public class AudioManager {
    public static String TAG = AudioManager.class.getName();

    private static AudioManager Instance;

    private final String root = "sound/";

    private LinkedHashMap<String, Sound> sounds;
    private LinkedHashMap<String, Music> musics;

    private AudioManager () {
        if (Instance == null) {
            Instance = new AudioManager();
        }
    }

    public static AudioManager getInstance() {
        return Instance;
    }


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

    public void playSound(String id) {

    }

    public void playMusic(String id) {

    }

    protected LinkedHashMap<String, Sound> getSounds() {
        return this.sounds;
    }

    protected LinkedHashMap<String, Music> getMusics() {
        return this.musics;
    }

    /**
     * sound里面的路径
     * */
    private FileHandle getFile (String path) {
        return Gdx.files.internal(this.getPath(path));
    }

    public String getPath (String path) {
        return this.root + path;
    }
}
