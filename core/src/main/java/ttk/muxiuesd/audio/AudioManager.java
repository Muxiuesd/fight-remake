package ttk.muxiuesd.audio;

/**
 * 全局声音管理
 * <p>
 * sound和music统称为声音
 * <p>
 * TODO Mod实现添加音效（乐）和播放音效（乐）
 * */
public class AudioManager {
    public static String TAG = AudioManager.class.getName();


    private static final class Holder {
        private static final AudioManager INSTANCE = new AudioManager();
    }

    public static AudioManager getInstance() {
        return Holder.INSTANCE;
    }






}
