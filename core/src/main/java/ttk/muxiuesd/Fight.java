package ttk.muxiuesd;

import ttk.muxiuesd.util.Info;

public class Fight {
    public static final String NAMESPACE = "fight";
    /// 游戏内部文件的根路径
    public static final String AUDIO_ROOT = "audio/";
    public static final String MUSIC_ROOT = AUDIO_ROOT + "music/";
    public static final String SOUND_ROOT = AUDIO_ROOT + "sound/";
    public static final String TEXTURE_ROOT = "texture/";
    public static final String BLOCK_TEXTURE_ROOT   = TEXTURE_ROOT + "blocks/";
    public static final String BOTANY_TEXTURE_ROOT   = TEXTURE_ROOT + "botany/";
    public static final String ENTITY_TEXTURE_ROOT  = TEXTURE_ROOT + "entity/";
    public static final String ITEM_TEXTURE_ROOT    = TEXTURE_ROOT + "item/";
    public static final String UI_TEXTURE_ROOT      = TEXTURE_ROOT + "ui/";
    public static final String FONT_ROOT = "font/";
    public static final String LANG_ROOT = "lang/";

    //世界的名称，也会作为存档文件夹的名称
    public static final Info<String> WORLD_NAME = Info.create("world_name", "null_world");
    public static final Info<Long> WORLD_SEED = Info.create("world_seed", 114514L);
    //UI的调试框是否渲染
    public static final Info<Boolean> UI_DEBUG_BOX_RENDER = Info.create("ui_debug_box_render", true);
    //玩家的听觉范围，单位：世界中的1米
    public static final Info<Float> PLAYER_HEARING_RANGE = Info.create("player_hearing_range", 16f);
    //物品实体最大存活时间，单位：秒
    public static final Info<Float> MAX_ITEM_ENTITY_LIVING_TIME = Info.create("max_item_entity_living_time", 300f);
    //玩家捡起掉落物的范围，单位：世界的一米
    public static final Info<Float> PLAYER_PICKUP_RANGE = Info.create("player_pickup_item_range", 1.5f);
    //掉落物可以被捡起来的cd，单位：秒
    public static final Info<Float> ITEM_ENTITY_PICKUP_SPAN = Info.create("item_entity_pickup_span", 2f);
    //玩家视野，单位：区块
    public static final Info<Integer> PLAYER_VISUAL_RANGE = Info.create("player_vision", 4);

    //实体渲染距离，与玩家距离超过这个值的实体不渲染
    public static final Info<Float> ENTITY_RENDER_RANGE = Info.create("entity_render_range", 20f);
    //实体更新距离，与玩家距离超过这个值的实体不更新
    public static final Info<Float> ENTITY_UPDATE_RANGE = Info.create("entity_update_range", 32f);
    //空气摩擦力
    public static final Info<Float> AIR_FRICTION= Info.create("air_friction", 0.03f);

    /**
     * 存档路径常量
     */
    public static final String PATH_SAVE = "save/";
    public static final String PATH_SAVE_WORLD = "world/";
    public static final String PATH_SAVE_CHUNKS = "chunks/";
    public static final String PATH_SAVE_ENTITIES = "entities/";
    public static final String PATH_SAVE_PLAYER = "player/";

    /**
     *  获取玩家数据保存的路径
     * */
    public static String getPathSavePlayer () {
        return getPathSaveEntities() + PATH_SAVE_PLAYER;
    }
    /**
     *  获取实体数据保存的路径
     * */
    public static String getPathSaveEntities () {
        return getPathSaveWorld() + PATH_SAVE_ENTITIES;
    }
    /**
     *  获取区块数据保存的路径
     * */
    public static String getPathSaveChunks () {
        return getPathSaveWorld() + PATH_SAVE_CHUNKS;
    }
    /**
     *  获取游戏世界数据保存路径，根据当前的世界名字作为值
     * */
    public static String getPathSaveWorld () {
        return PATH_SAVE + WORLD_NAME.getValue() + "/" + PATH_SAVE_WORLD;
    }

    /**
     * 游戏本体的元素获取ID
     * */
    public static String ID (String name) {
        synchronized (idStringBuilder) {
            idStringBuilder.setLength(0);
            idStringBuilder.append(NAMESPACE).append(":").append(name);
            return idStringBuilder.toString();
        }
    }
    private static final StringBuilder idStringBuilder = new StringBuilder();


    /**
     * 从方块的材质根路径中获取方块的材质
     * */
    public static String BlockTexturePath (String path) {
        return BLOCK_TEXTURE_ROOT + path;
    }

    /**
     * 从植物的材质根路径中获取植物的材质
     * */
    public static String BotanyTexturePath (String path) {
        return BOTANY_TEXTURE_ROOT + path;
    }

    public static String EntityTexturePath (String path) {
        return ENTITY_TEXTURE_ROOT + path;
    }

    public static String ItemTexturePath (String path) {
        return ITEM_TEXTURE_ROOT + path;
    }

    public static String UITexturePath (String path) { return UI_TEXTURE_ROOT + path; }

    public static String FontPath (String path) {
        if (path.endsWith(".ttf")) return FONT_ROOT + path;
        return FONT_ROOT + path + ".ttf";
    }

    public static String LangPath (String path) {
        if (path.endsWith(".json")) return LANG_ROOT + path;
        return LANG_ROOT + path + ".json";
    }

    public static String MusicPath (String path) {
        return MUSIC_ROOT + path;
    }

    public static String SoundPath (String path) {
        return SOUND_ROOT + path;
    }

    public static String AudioPath (String path) {
        return AUDIO_ROOT + path;
    }

    public static String GameSavePath (String name) {
        return PATH_SAVE + name;
    }
}
