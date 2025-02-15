package ttk.muxiuesd.audio;

/**
 * 声音注册
 * */
public class AudioReg {

    public static final AudioManager manager = AudioManager.getInstance();

    public static void registerAllAudios () {
        //玩家
        registerSound("shoot", "player/shoot.wav");

        //方块
        registerSound("grass_walk", "block/walk/grass.wav");
    }

    public static void registerSound (String id, String path) {
        manager.loadSound(id, "sound/" + path);
    }

    public static void registerMusic (String id, String path) {
        manager.loadMusic(id,  "music/" + path);
    }
}
