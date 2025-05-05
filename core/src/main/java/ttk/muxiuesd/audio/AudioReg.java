package ttk.muxiuesd.audio;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.BlockSoundsID;

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
        registerSoundAsMusic("eat_1",  "entity/eat/eat1.ogg");
        registerSoundAsMusic("eat_2",  "entity/eat/eat2.ogg");
        registerSoundAsMusic("eat_3",  "entity/eat/eat3.ogg");

        //玩家
        registerSoundAsMusic("shoot", "player/shoot.wav");
        registerMusic("player_resurrection", "player_resurrection.mp3");

        //方块
        registerBlockSound("stone_walk_2", "walk/stone.wav");
        registerBlockSound("stone_walk_1", "walk/stone_1.ogg");

        registerBlockSoundAsMusic("stone_walk", "walk/stone.ogg");
        registerBlockSoundAsMusic("stone_put", "put/stone.ogg");
        registerBlockSoundAsMusic("stone_destroy", "destroy/stone.ogg");

        //物品
        registerSoundAsMusic("click", "item/click.ogg");
        registerSound("put", "item/put.ogg");


        Log.print(AudioReg.class.getName(), "游戏音效注册完成");
    }

    public static final BlockSoundsID SAND =  fastRegisterBlockSoundAsMusic("sand", "sand.ogg");
    public static final BlockSoundsID GRASS =  fastRegisterBlockSoundAsMusic("grass", "grass.ogg");
    /**
     * 快速注册方块的所有音效
     * @return 返回注册好的方块音效类
     * */
    public static BlockSoundsID fastRegisterBlockSoundAsMusic (String name, String soundFileName) {

        BlockSoundsID ids = new BlockSoundsID(
            Fight.getId(name+"_walk"),
            Fight.getId(name+"_put"),
            Fight.getId(name+"_destroy"));
        registerBlockSoundAsMusic(name+"_walk", "walk/" + soundFileName);
        registerBlockSoundAsMusic(name+"_put", "put/" + soundFileName);
        registerBlockSoundAsMusic(name+"_destroy", "destroy/" + soundFileName);
        return ids;
    }

    /**
     * 把方块sound加载成music
     * */
    public static void registerBlockSoundAsMusic (String name, String path) {
        registerSoundAsMusic(name, "block/" + path);
    }

    /**
     * 注册方块sound
     * */
    public static void registerBlockSound (String name, String path) {
        registerSound(name, "block/" + path);
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
