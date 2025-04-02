package ttk.muxiuesd.audio;

import ttk.muxiuesd.Fight;

/**
 * 声音注册
 * */
public class AudioReg {

    public static final AudioLoader loader = AudioLoader.getInstance();

    public static void registerAllAudios () {
        //实体
        registerSoundAsMusic("hurt_1",  "entity/damage/hit_1.ogg");
        registerSoundAsMusic("hurt_2",  "entity/damage/hit_2.ogg");
        registerSoundAsMusic("hurt_3",  "entity/damage/hit_3.ogg");

        //玩家
        registerSoundAsMusic("shoot", "player/shoot.wav");
        registerMusic("player_resurrection", "player_resurrection.mp3");

        //方块
        registerSound("grass_walk", "block/walk/grass.wav");
        registerSound("stone_walk", "block/walk/stone.wav");
        registerSound("stone_walk_1", "block/walk/stone_1.ogg");

        //物品
        registerSoundAsMusic("click", "item/click.ogg");
    }

    public static void registerSound (String name, String path) {
        loader.loadSound(Fight.getId(name), "sound/" + path);
    }

    /**
     * 把sound加载成music
     * */
    public static void registerSoundAsMusic (String name, String path) {
        loader.loadMusic(Fight.getId(name), "sound/" + path);
    }

    public static void registerMusic (String name, String path) {
        loader.loadMusic(Fight.getId(name), "music/" + path);
    }
}
