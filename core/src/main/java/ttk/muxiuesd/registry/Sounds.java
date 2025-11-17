package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.BlockSoundsID;

/**
 * 音效的注册
 * */
public final class Sounds {
    public static void init () {
    }

    public static final AudioLoader LOADER = AudioLoader.getInstance();

    ///实体
    public static final Audio ENTITY_HURT_1 = registerSoundAsMusic("hurt_1",  "entity/damage/hit_1.ogg");
    public static final Audio ENTITY_HURT_2 = registerSoundAsMusic("hurt_2",  "entity/damage/hit_2.ogg");
    public static final Audio ENTITY_HURT_3 = registerSoundAsMusic("hurt_3",  "entity/damage/hit_3.ogg");
    public static final Audio ENTITY_EAT_1 = registerSoundAsMusic("eat_1",  "entity/eat/eat1.ogg");
    public static final Audio ENTITY_EAT_2  = registerSoundAsMusic("eat_2",  "entity/eat/eat2.ogg");
    public static final Audio ENTITY_EAT_3 = registerSoundAsMusic("eat_3",  "entity/eat/eat3.ogg");

    public static final Audio ENTITY_SLIME_SMALL = registerSoundAsMusic("slime_small",  "entity/slime/small.ogg");

    ///玩家
    public static final Audio ENTITY_SHOOT = registerSoundAsMusic("shoot", "player/shoot.wav");
    public static final Audio ENTITY_SWEEP = registerSoundAsMusic("sweep", "player/sweep.ogg");
    public static final Audio PLAYER_KILL = registerSoundAsMusic("player_kill", "player/kill.wav");
    public static final Audio PLAYER_RESURRECTION = registerMusic("player_resurrection", "player_resurrection.mp3");

    ///物品
    public static final Audio ITEM_CLICK = registerSoundAsMusic("item_click", "item/click.ogg");
    public static final Audio ITEM_PUT = registerSoundAsMusic("item_put", "item/put.ogg");
    public static final Audio ITEM_POP = registerSoundAsMusic("item_pop", "item/pop.ogg");
    public static final Audio ITEM_BREAK = registerSoundAsMusic("item_break", "item/break.ogg");

    ///装备物品
    public static final Audio EQUIP = registerSound("equip", "item/equipment/equip.ogg");

    ///方块
    public static final BlockSoundsID STONE = registerBlockSounds("stone", "stone.ogg");
    public static final BlockSoundsID SAND =  registerBlockSounds("sand", "sand.ogg");
    public static final BlockSoundsID GRASS =  registerBlockSounds("grass", "grass.ogg");


    public static BlockSoundsID registerBlockSounds (String name, String soundFileName) {
        String walk = name + "_walk";
        String put = name + "_put";
        String destroy = name + "_destroy";
        BlockSoundsID ids = new BlockSoundsID(Fight.ID(walk), Fight.ID(put), Fight.ID(destroy));
        registerBlockSoundAsMusic(walk, "walk/" + soundFileName);
        registerBlockSoundAsMusic(put, "put/" + soundFileName);
        registerBlockSoundAsMusic(destroy, "destroy/" + soundFileName);

        return Registries.BLOCK_SOUNDS.register(new Identifier(Fight.NAMESPACE, name), ids);
    }

    /**
     * 把方块sound加载成music
     * */
    public static Audio registerBlockSoundAsMusic (String name, String path) {
        return registerSoundAsMusic(name, "block/" + path);
    }

    /**
     * 注册方块sound
     * */
    public static Audio registerBlockSound (String name, String path) {
        return registerSound(name, "block/" + path);
    }

    /**
     * 把sound加载成music
     * */
    public static Audio registerSoundAsMusic (String name, String path) {
        String id = LOADER.loadMusic(Fight.ID(name), "sound/" + path);
        return register(id);
    }


    /**
     * 最基础的sound注册
     * */
    public static Audio registerSound (String name, String path) {
        String id = LOADER.loadSound(Fight.ID(name), "sound/" + path);
        return register(id);
    }

    /**
     * 最基础的music注册
     * */
    public static Audio registerMusic (String name, String path) {
        String id = LOADER.loadMusic(Fight.ID(name), "music/" + path);
        return register(id);
    }

    /**
     * 最基础音效注册
     * */
    public static Audio register (String id) {
        return Registries.AUDIOS.register(new Identifier(id), new Audio(id));
    }
}
