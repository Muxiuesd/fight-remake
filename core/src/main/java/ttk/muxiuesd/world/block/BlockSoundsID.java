package ttk.muxiuesd.world.block;

import ttk.muxiuesd.Fight;

/**
 * 方块音效类
 * */
public class BlockSoundsID {
    //默认都为石头的音效
    public static final BlockSoundsID DEFAULT = new BlockSoundsID(new String[]{
        Fight.getId("stone_walk"),
        Fight.getId("stone_put"),
        Fight.getId("stone_destroy")});

    public enum Type {
        WALK(0),
        PUT(1),
        DESTROY(2)
        ;

        final int num;
        Type (int num) {
            this.num = num;
        }
    }

    private String[] ids;

    public BlockSoundsID(String walk, String put, String destroy) {
        this(new String[]{walk, put, destroy});
    }

    public BlockSoundsID (String[] ids) {
        this.ids = ids;
    }

    public BlockSoundsID () {
    }

    /**
     * 获取对应的音效id
     * */
    public String getID (Type type) {
        return this.ids[type.num];
    }

    public BlockSoundsID setIds (String[] ids) {
        this.ids = ids;
        return this;
    }
}
