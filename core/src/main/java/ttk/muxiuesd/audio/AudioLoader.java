package ttk.muxiuesd.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 声音加载器
 * */
public class AudioLoader {
    public final String TAG = this.getClass().getName();

    private final String root = "audio/";

    private final LinkedHashMap<String, Sound> soundCache = new LinkedHashMap<>();
    private final LinkedHashMap<String, Music> musicCache = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> soundIdToPath = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> musicIdToPath = new LinkedHashMap<>();


    private AudioLoader() {}
    private static final class Holder {
        private static final AudioLoader INSTANCE = new AudioLoader();
    }
    public static AudioLoader getInstance() {
        return AudioLoader.Holder.INSTANCE;
    }

    /**
     * 加载音效
     * @param id    音效的id
     * @param path  音效在audio文件夹里的路径
     * */
    public void loadSound(String id, String path) {
        if (getInstance().getSoundCache().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音效已经存在！！！");
            return;
        }
        AssetsLoader loader = AssetsLoader.getInstance();
        String soundPath = this.getPath(path);
        Class<Sound> soundClass = Sound.class;
        loader.loadAsync(soundPath, soundClass, () -> {
            Sound sound = loader.get(soundPath, soundClass);
            getInstance().getSoundCache().put(id, sound);
        });

        this.soundIdToPath.put(id, soundPath);
    }

    /**
     * 加载音乐
     * @param id    音乐的id
     * @param path  音乐在audio文件夹里的路径
     * */
    public void loadMusic(String id, String path) {
        if (getInstance().getMusicCache().containsKey(id)) {
            Log.error(TAG, "Id为：" + id + " 的音乐已经存在！！！");
            return;
        }
        AssetsLoader loader = AssetsLoader.getInstance();
        String musicPath = this.getPath(path);
        Class<Music> musicClass = Music.class;
        loader.loadAsync(musicPath, musicClass, () -> {
            Music music = loader.get(musicPath, musicClass);
            getInstance().getMusicCache().put(id, music);
        });

        this.musicIdToPath.put(id, musicPath);
    }

    public LinkedHashMap<String, Sound> getSoundCache () {
        return this.soundCache;
    }

    public LinkedHashMap<String, Music> getMusicCache () {
        return this.musicCache;
    }

    /**
     * 获得指定的音效
     * */
    public Sound getSound(String id) {
        return this.getSoundCache().get(id);
    }

    /**
     * 获得指定的音乐
     * */
    public Music getMusic(String id) {
        return this.getMusicCache().get(id);
    }

    /**
     * 新建一个sound
     * */
    public Sound newSound (String id) {
        String path = this.soundIdToPath.get(id);
        if (path == null) {
            Log.error(TAG, "Id为：" + id + " 的sound不存在！！！");
            return null;
        }
        //return AssetsLoader.getInstance().get(path, Sound.class);
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    /**
     * 新建一个music
     * */
    public Music newMusic (String id) {
        String path = this.musicIdToPath.get(id);
        if (path == null) {
            Log.error(TAG, "Id为：" + id + " 的music不存在！！！");
            return null;
        }
        //return AssetsLoader.getInstance().get(path, Music.class);
        return Gdx.audio.newMusic(Gdx.files.internal(path));
    }

    public String getPath (String path) {
        return this.root + path;
    }
}
