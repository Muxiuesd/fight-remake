package ttk.muxiuesd;

public class Fight {
    public static final String NAMESPACE = "fight";
    public static final float HEARING_RANGE = 16f;  //玩家的听觉范围，单位：世界中的1米
    public static final String BLOCK_TEXTURE_ROOT = "texture/blocks/";
    public static final String ENTITY_TEXTURE_ROOT = "texture/entity/";
    public static final String ITEM_TEXTURE_ROOT = "texture/item/";



    public static String getId (String name) {
        return NAMESPACE + ":" + name;
    }

    /**
     * 从方块的材质根路径中获取方块的材质
     * */
    public static String getBlockTexture (String path) {
        return BLOCK_TEXTURE_ROOT + path;
    }

    public static String getEntityTexture (String path) {
        return ENTITY_TEXTURE_ROOT + path;
    }

    public static String getItemTexture (String path) {
        return ITEM_TEXTURE_ROOT + path;
    }
}
