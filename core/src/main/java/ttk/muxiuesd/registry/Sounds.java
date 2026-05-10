package ttk.muxiuesd.registry;

import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.block.BlockSounds;

/**
 * 音效的注册
 * */
public final class Sounds {
    public static void init () {
    }

    public static final AudioLoader LOADER = AudioLoader.getInstance();

    ///实体
    public static final AudioHolder ENTITY_HURT_1 = registerSound("hurt_1",  "entity/damage/hit_1.ogg");
    public static final AudioHolder ENTITY_HURT_2 = registerSound("hurt_2",  "entity/damage/hit_2.ogg");
    public static final AudioHolder ENTITY_HURT_3 = registerSound("hurt_3",  "entity/damage/hit_3.ogg");
    public static final AudioHolder ENTITY_EAT_1  = registerSound("eat_1",  "entity/eat/eat1.ogg");
    public static final AudioHolder ENTITY_EAT_2  = registerSound("eat_2",  "entity/eat/eat2.ogg");
    public static final AudioHolder ENTITY_EAT_3  = registerSound("eat_3",  "entity/eat/eat3.ogg");

    public static final AudioHolder ENTITY_SLIME_SMALL = registerSound("slime_small",  "entity/slime/small.ogg");

    ///玩家
    public static final AudioHolder ENTITY_SHOOT = registerSound("shoot", "player/shoot.wav");
    public static final AudioHolder ENTITY_SWEEP = registerSound("sweep", "player/sweep.ogg");
    public static final AudioHolder PLAYER_KILL = registerSound("player_kill", "player/kill.wav");
    public static final AudioHolder PLAYER_RESURRECTION = registerMusic("player_resurrection", "player_resurrection.mp3");

    ///物品
    public static final AudioHolder ITEM_CLICK = registerSound("item_click", "item/click.ogg");
    public static final AudioHolder ITEM_PUT = registerSound("item_put", "item/put.ogg");
    public static final AudioHolder ITEM_POP = registerSound("item_pop", "item/pop.ogg");
    public static final AudioHolder ITEM_BREAK = registerSound("item_break", "item/break.ogg");

    ///装备物品
    public static final AudioHolder EQUIP = registerSound("equip", "item/equipment/equip.ogg");

    ///方块相关音效
    //草
    public static final AudioHolder GRASS_1 = registerSound("grass_1",  "block/grass_1.ogg");
    public static final AudioHolder GRASS_2 = registerSound("grass_2",  "block/grass_2.ogg");
    public static final AudioHolder GRASS_3 = registerSound("grass_3",  "block/grass_3.ogg");
    public static final AudioHolder GRASS_4 = registerSound("grass_4",  "block/grass_4.ogg");
    //方块
    public static final BlockSounds STONE =  registerBlockSounds("stone", "stone.ogg");
    public static final BlockSounds SAND =   registerBlockSounds("sand", "sand.ogg");
    public static final BlockSounds GRASS =  registerBlockSounds("grass", "grass.ogg");


    /**
     * 注册方块相关的音效集合
     * */
    public static BlockSounds registerBlockSounds (String name, String soundFileName) {
        String walk = name + "_walk";
        String put = name + "_put";
        String destroy = name + "_destroy";
        BlockSounds sounds = new BlockSounds(
            registerBlockSound(walk, "walk/" + soundFileName),
            registerBlockSound(put, "put/" + soundFileName),
            registerBlockSound(destroy, "destroy/" + soundFileName)
        );

        return Registries.BLOCK_SOUNDS.register(new Identifier(Fight.ID(name)), sounds);
    }

    /**
     * 注册方块相关的音效
     * @param filePath audio/sound/block/ 目录下的文件路径
     * */
    public static AudioHolder registerBlockSound (String name, String filePath) {
        return registerSound(name, "block/" + filePath);
    }

    /*public static BlockSoundsID registerBlockSounds (String name, String soundFileName) {
        String walk = name + "_walk";
        String put = name + "_put";
        String destroy = name + "_destroy";
        BlockSoundsID ids = new BlockSoundsID(
            Fight.ID(walk),
            Fight.ID(put),
            Fight.ID(destroy)
        );
        registerBlockSoundAsMusic(walk, "walk/" + soundFileName);
        registerBlockSoundAsMusic(put, "put/" + soundFileName);
        registerBlockSoundAsMusic(destroy, "destroy/" + soundFileName);

        return Registries.BLOCK_SOUNDS.register(new Identifier(Fight.NAMESPACE, name), ids);
    }

    *//**
     * 把方块sound加载成music
     * *//*
    public static AudioHolder registerBlockSoundAsMusic (String name, String path) {
        return registerSoundAsMusic(name, "block/" + path);
    }

    *//**
     * 注册方块sound
     * *//*
    public static AudioHolder registerBlockSound (String name, String path) {
        return registerSound(name, "block/" + path);
    }

    *//**
     * 把sound加载成music
     * *//*
    public static AudioHolder registerSoundAsMusic (String name, String path) {
        String id = LOADER.loadMusic(Fight.ID(name), "sound/" + path);
        return register(id);
    }*/


    /**
     * 最基础的sound注册
     *
    public static AudioHolder registerSound (String name, String path) {
        String id = LOADER.loadSound(Fight.ID(name), "sound/" + path);
        return register(id);
    }

    *//**
     * 最基础的music注册
     * *//*
    public static AudioHolder registerMusic (String name, String path) {
        String id = LOADER.loadMusic(Fight.ID(name), "music/" + path);
        return register(id);
    }*/

    /**
     * 注册游戏的音乐
     * @param filePath 基于audio/music/文件夹下的文件路径
     * */
    public static AudioHolder registerMusic (String name, String filePath) {
        return register(Fight.ID(name), FileUtil.getFileHandle(Fight.MusicPath(filePath)));
    }

    /**
     * 注册游戏的音效
     * @param filePath 基于audio/sound/文件夹下的文件路径
     * */
    public static AudioHolder registerSound (String name, String filePath) {
        return register(Fight.ID(name), FileUtil.getFileHandle(Fight.SoundPath(filePath)));
    }

    /**
     * 最基础音效注册
     * @param id                音频id
     * @param audioFileHandle   音频的文件持有
     * */
    public static AudioHolder register (String id, FileHandle audioFileHandle) {
        Identifier identifier = new Identifier(id);
        AudioHolder audioHolder = new AudioHolder(identifier);
        audioHolder.setFileHandle(audioFileHandle);
        return Registries.AUDIOS.register(identifier, audioHolder);
    }
}
