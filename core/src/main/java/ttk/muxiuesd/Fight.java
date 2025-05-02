package ttk.muxiuesd;

public class Fight {
    public static final String NAMESPACE = "fight";
    public static final String AUDIO_ROOT = "audio/";
    public static final String BLOCK_TEXTURE_ROOT = "texture/blocks/";
    public static final String ENTITY_TEXTURE_ROOT = "texture/entity/";
    public static final String ITEM_TEXTURE_ROOT = "texture/item/";

    public static final float HEARING_RANGE = 16f;  //玩家的听觉范围，单位：世界中的1米
    public static final float MAX_ITEM_ENTITY_LIVING_TIME = 120f;   //物品实体最大存活时间，单位：秒

    public static final float PICKUP_RANGE = 1.5f;  //玩家捡起掉落物的范围
    public static final float ITEM_ENTITY_PICKUP_SPAN = 3f;     //掉落物可以被捡起来的cd

    public static float PLAYER_VISUAL_RANGE = 64f;

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
}
