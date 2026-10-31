package ttk.muxiuesd.registry;

import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.SoundGroup;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.InternalFileUtil;
import ttk.muxiuesd.world.block.BlockSounds;

/**
 * 音效的注册
 * <p>
 * 目前只能注册打包进jar的音效文件！
 * */
public final class Sounds {
    public static void init () {
        Log.print(Sounds.class.getName(), "游戏音频注册完毕");
    }

    ///实体
    public static final AudioHolder ENTITY_HURT_1 = registerSound("hurt_1",  "entity/damage/hit_1.ogg");
    public static final AudioHolder ENTITY_HURT_2 = registerSound("hurt_2",  "entity/damage/hit_2.ogg");
    public static final AudioHolder ENTITY_HURT_3 = registerSound("hurt_3",  "entity/damage/hit_3.ogg");
    public static final AudioHolder ENTITY_EAT_1  = registerSound("eat_1",  "entity/eat/eat1.ogg");
    public static final AudioHolder ENTITY_EAT_2  = registerSound("eat_2",  "entity/eat/eat2.ogg");
    public static final AudioHolder ENTITY_EAT_3  = registerSound("eat_3",  "entity/eat/eat3.ogg");
    public static final AudioHolder ENTITY_DRINK  = registerSound("drink",  "entity/drink/drink.ogg");

    public static final AudioHolder ENTITY_SLIME_SMALL = registerSound("slime_small",  "entity/slime/small.ogg");

    ///玩家
    public static final AudioHolder ENTITY_SHOOT = registerSound("shoot", "player/shoot.wav");
    public static final AudioHolder ENTITY_SWEEP = registerSound("sweep", "player/sweep.ogg");
    public static final AudioHolder PLAYER_KILL = registerSound("player_kill", "player/kill.wav");
    public static final AudioHolder PLAYER_RESURRECTION = registerMusic("player_resurrection", "player_resurrection.mp3");

    ///物品
    public static final AudioHolder ITEM_CLICK = registerSound("item_click", "item/click.ogg");
    public static final AudioHolder ITEM_PUT   = registerSound("item_put", "item/put.ogg");
    public static final AudioHolder ITEM_POP   = registerSound("item_pop", "item/pop.ogg");
    public static final AudioHolder ITEM_BREAK = registerSound("item_break", "item/break.ogg");
    public static final AudioHolder CROP_PLANT_1 = registerSound("crop_plant_1",  "item/crop/crop_plant_1.ogg");
    public static final AudioHolder CROP_PLANT_2 = registerSound("crop_plant_2",  "item/crop/crop_plant_2.ogg");
    public static final AudioHolder CROP_PLANT_3 = registerSound("crop_plant_3",  "item/crop/crop_plant_3.ogg");
    public static final AudioHolder CROP_PLANT_4 = registerSound("crop_plant_4",  "item/crop/crop_plant_4.ogg");
    public static final AudioHolder CROP_PLANT_5 = registerSound("crop_plant_5",  "item/crop/crop_plant_5.ogg");
    public static final AudioHolder CROP_PLANT_6 = registerSound("crop_plant_6",  "item/crop/crop_plant_6.ogg");

    ///装备物品
    public static final AudioHolder EQUIP = registerSound("equip", "item/equipment/equip.ogg");

    ///方块相关音效
    //草
    public static final AudioHolder GRASS_DESTROY_1 = registerBlockSound("grass_destroy_1",  "grass/grass_destroy_1.ogg");
    public static final AudioHolder GRASS_DESTROY_2 = registerBlockSound("grass_destroy_2",  "grass/grass_destroy_2.ogg");
    public static final AudioHolder GRASS_DESTROY_3 = registerBlockSound("grass_destroy_3",  "grass/grass_destroy_3.ogg");
    public static final AudioHolder GRASS_DESTROY_4 = registerBlockSound("grass_destroy_4",  "grass/grass_destroy_4.ogg");
    //农作物
    public static final AudioHolder CROP_BREAK_1 = registerBlockSound("crop_break_1",  "crop/crop_break_1.ogg");
    public static final AudioHolder CROP_BREAK_2 = registerBlockSound("crop_break_2",  "crop/crop_break_2.ogg");
    public static final AudioHolder CROP_BREAK_3 = registerBlockSound("crop_break_3",  "crop/crop_break_3.ogg");
    public static final AudioHolder CROP_BREAK_4 = registerBlockSound("crop_break_4",  "crop/crop_break_4.ogg");
    public static final AudioHolder CROP_BREAK_5 = registerBlockSound("crop_break_5",  "crop/crop_break_5.ogg");
    public static final AudioHolder CROP_BREAK_6 = registerBlockSound("crop_break_6",  "crop/crop_break_6.ogg");
    ///方块音效，每种音效的id手写固定，与注册顺序无关
    //石头：走路两首，放置、破坏各一首
    public static final AudioHolder STONE_WALK     = registerBlockSound("stone_walk",     "walk/stone.ogg");
    public static final AudioHolder STONE_WALK_2   = registerBlockSound("stone_walk_2",   "walk/stone_1.ogg");
    public static final AudioHolder STONE_PUT      = registerBlockSound("stone_put",      "put/stone.ogg");
    public static final AudioHolder STONE_DESTROY  = registerBlockSound("stone_destroy",  "destroy/stone.ogg");
    //沙子
    public static final AudioHolder SAND_WALK =      registerBlockSound("sand_walk",      "walk/sand.ogg");
    public static final AudioHolder SAND_PUT =       registerBlockSound("sand_put",       "put/sand.ogg");
    public static final AudioHolder SAND_DESTROY =   registerBlockSound("sand_destroy",   "destroy/sand.ogg");
    //草：走路两首，放置、破坏各一首
    public static final AudioHolder GRASS_WALK =     registerBlockSound("grass_walk",     "walk/grass.ogg");
    public static final AudioHolder GRASS_WALK_2 =   registerBlockSound("grass_walk_2",   "walk/grass_2.wav");
    public static final AudioHolder GRASS_PUT =      registerBlockSound("grass_put",      "put/grass.ogg");
    public static final AudioHolder GRASS_DESTROY =  registerBlockSound("grass_destroy",  "destroy/grass.ogg");

    /// 方块音效集合，只把上面已经注册好的音效分组，不碰文件也不拼id
    public static final BlockSounds STONE = registerBlockSounds("stone",
        SoundGroup.of(STONE_WALK),
        SoundGroup.of(STONE_PUT),
        SoundGroup.of(STONE_DESTROY)
    );
    public static final BlockSounds SAND = registerBlockSounds("sand",
        SoundGroup.of(SAND_WALK),
        SoundGroup.of(SAND_PUT),
        SoundGroup.of(SAND_DESTROY)
    );
    public static final BlockSounds GRASS = registerBlockSounds("grass",
        SoundGroup.of(GRASS_WALK),
        SoundGroup.of(GRASS_PUT),
        SoundGroup.of(GRASS_DESTROY_1, GRASS_DESTROY_2, GRASS_DESTROY_3, GRASS_DESTROY_4)
    );
    public static final BlockSounds CROP = registerBlockSounds("crop",
        SoundGroup.of(GRASS_WALK),
        SoundGroup.of(GRASS_PUT),
        SoundGroup.of(CROP_BREAK_1, CROP_BREAK_2, CROP_BREAK_3, CROP_BREAK_4, CROP_BREAK_5, CROP_BREAK_6)
    );



    /**
     * 注册方块相关的音效集合
     * @param name      方块音效集合的id
     * @param walk      走路音效组
     * @param put       放置音效组
     * @param destroy   破坏音效组
     * */
    public static BlockSounds registerBlockSounds (String name, SoundGroup walk, SoundGroup put, SoundGroup destroy) {
        BlockSounds sounds = new BlockSounds(walk, put, destroy);
        Identifier identifier = Identifier.of(Fight.ID(name));
        sounds.setIdentifier(identifier);
        return Registries.BLOCK_SOUNDS.register(identifier, sounds);
    }

    /**
     * 注册方块相关的音效
     * @param filePath audio/sound/block/ 目录下的文件路径
     * */
    public static AudioHolder registerBlockSound (String name, String filePath) {
        return registerSound(name, "block/" + filePath);
    }

    /**
     * 注册游戏的音乐
     * @param filePath 基于audio/music/文件夹下的文件路径
     * */
    public static AudioHolder registerMusic (String name, String filePath) {
        //return register(Fight.ID(name), AbsFileUtil.getFileHandle(Fight.MusicPath(filePath)));
        return register(Fight.ID(name), InternalFileUtil.getInstance().getFileHandle(Fight.MusicPath(filePath)));
    }

    /**
     * 注册游戏的音效
     * @param filePath 基于audio/sound/文件夹下的文件路径
     * */
    public static AudioHolder registerSound (String name, String filePath) {
        return register(Fight.ID(name), InternalFileUtil.getInstance().getFileHandle(Fight.SoundPath(filePath)));
    }

    /**
     * 最基础音效注册
     * @param id                音频id
     * @param audioFileHandle   音频的文件持有
     * */
    public static AudioHolder register (String id, FileHandle audioFileHandle) {
        Identifier identifier = Identifier.of(id);
        AudioHolder audioHolder = new AudioHolder(identifier);
        audioHolder.setFileHandle(audioFileHandle);
        return Registries.AUDIOS.register(identifier, audioHolder);
    }
}
