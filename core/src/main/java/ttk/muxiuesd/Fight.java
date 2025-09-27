package ttk.muxiuesd;

import ttk.muxiuesd.util.Info;

public class Fight {
    public static final String NAMESPACE = "fight";
    public static final String AUDIO_ROOT = "audio/";
    public static final String BLOCK_TEXTURE_ROOT = "texture/blocks/";
    public static final String ENTITY_TEXTURE_ROOT = "texture/entity/";
    public static final String ITEM_TEXTURE_ROOT = "texture/item/";
    public static final String UI_TEXTURE_ROOT = "texture/ui/";

    //玩家的听觉范围，单位：世界中的1米
    public static final Info<Float> PLAYER_HEARING_RANGE = Info.create("player_hearing_range", 16f);
    //物品实体最大存活时间，单位：秒
    public static final Info<Float> MAX_ITEM_ENTITY_LIVING_TIME = Info.create("max_item_entity_living_time", 3f);
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

    /**
     * 存档路径常量
     */
    public static final String PATH_SAVE = "save/";
    public static final String PATH_SAVE_WORLD = PATH_SAVE + "world/";
    public static final String PATH_SAVE_CHUNKS = PATH_SAVE_WORLD + "chunks/";
    public static final String PATH_SAVE_ENTITIES = PATH_SAVE_WORLD + "entities/";
    public static final String PATH_SAVE_PLAYER = PATH_SAVE_ENTITIES + "player/";



    public static String getId (String name) {
        return NAMESPACE + ":" + name;
    }

    /**
     * 从方块的材质根路径中获取方块的材质
     * */
    public static String BlockTexturePath (String path) {
        return BLOCK_TEXTURE_ROOT + path;
    }

    public static String EntityTexturePath (String path) {
        return ENTITY_TEXTURE_ROOT + path;
    }

    public static String ItemTexturePath (String path) {
        return ITEM_TEXTURE_ROOT + path;
    }

    public static String UITexturePath (String path) { return UI_TEXTURE_ROOT + path; }

    public static String GameSavePath (String name) {
        return PATH_SAVE + name;
    }
}
