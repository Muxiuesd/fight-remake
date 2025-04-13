package ttk.muxiuesd.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.mod.api.ModFileLoader;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 声音加载器
 * */
public class AudioLoader {
    public final String TAG = this.getClass().getName();

    private final String root;  //可以是游戏内的根，也可以是mod的根

    //全局静态缓存这些音频
    private static final LinkedHashMap<String, Sound> soundCache = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Music> musicCache = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> soundIdToPath = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> musicIdToPath = new LinkedHashMap<>();

    public AudioLoader(String root) {
        this.root = root;
    }

    private static final class Holder {
        private static final AudioLoader INSTANCE = new AudioLoader(Fight.AUDIO_ROOT);
    }
    //游戏源码调用的实例
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

        String soundPath = this.getPath(path);
        Class<Sound> soundClass = Sound.class;
        if (this.inGame(id)) {
            //游戏内部的音频加载
            AssetsLoader loader = AssetsLoader.getInstance();
            loader.loadAsync(id, soundPath, soundClass, () -> {
                Sound sound = loader.getById(id, soundClass);
                getInstance().getSoundCache().put(id, sound);
            });
        }else {
            //mod的音频加载
            ModFileLoader modFileLoader = ModFileLoader.getFileLoader(id.split(":")[0]);
            modFileLoader.load(id, path, soundClass, () -> {
                Sound sound = AssetsLoader.getInstance().getById(id, soundClass);
                getInstance().getSoundCache().put(id, sound);
            });
        }
        //添加映射
        soundIdToPath.put(id, soundPath);
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

        String musicPath = this.getPath(path);
        Class<Music> musicClass = Music.class;
        if (this.inGame(id)) {
            //游戏内部的音频加载
            AssetsLoader loader = AssetsLoader.getInstance();

            loader.loadAsync(id, musicPath, musicClass, () -> {
                Music music = loader.getById(id, musicClass);
                getInstance().getMusicCache().put(id, music);
            });
        }else {
            //mod的音频加载
            ModFileLoader modFileLoader = ModFileLoader.getFileLoader(id.split(":")[0]);
            modFileLoader.load(id, path, musicClass, () -> {
                Music music = AssetsLoader.getInstance().getById(id, musicClass);
                getInstance().getMusicCache().put(id, music);
            });
        }
        //添加映射
        musicIdToPath.put(id, musicPath);
    }

    public LinkedHashMap<String, Sound> getSoundCache () {
        return soundCache;
    }

    public LinkedHashMap<String, Music> getMusicCache () {
        return musicCache;
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
        String path = soundIdToPath.get(id);
        if (path == null) {
            Log.error(TAG, "Id为：" + id + " 的Sound不存在！！！");
            throw new IllegalStateException("Id为：" + id + " 的Sound不存在！！！");
        }
        //return AssetsLoader.getInstance().get(path, Sound.class);
        return Gdx.audio.newSound(this.getFileHandle(id, path));
    }

    /**
     * 新建一个music
     * */
    public Music newMusic (String id) {
        String path = musicIdToPath.get(id);
        if (path == null) {
            Log.error(TAG, "Id为：" + id + " 的Music不存在！！！");
            throw new IllegalStateException("Id为：" + id + " 的Music不存在！！！");
        }
        //return AssetsLoader.getInstance().get(path, Music.class);
        return Gdx.audio.newMusic(this.getFileHandle(id, path));
    }

    /**
     * 根据不同的命名空间来区别游戏内和mod的文件处理
     * */
    public FileHandle getFileHandle(String id, String path) {
        if (this.inGame(id)) {
            return Gdx.files.internal(path);
        }
        return Gdx.files.absolute(path);
    }

    public String getPath (String path) {
        return this.root + path;
    }

    /**
     * id命名空间是否是游戏内部的
     * */
    public boolean inGame (String id) {
        String[] split = id.split(":");
        return Objects.equals(split[0], Fight.NAMESPACE);
    }
}
