package ttk.muxiuesd.registrant;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 注册器组管理器，这个类管理着游戏内所有的注册器
 * */
public class RegistrantGroup {
    public static final String TAG = RegistrantGroup.class.getName();

    public static final int OTHER  = -1;
    public static final int ENTITY = 0;
    public static final int BLOCK  = 1;

    enum Type {
        OTHER, ENTITY, BLOCK, WALL, ITEM
    }
    //总和
    private static final HashMap<Type, HashMap> res = new HashMap<>();

    private static final HashMap<String, Registrant<? extends Test>> testRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Entity>> entityRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Block>> blockRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Wall>> wallRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Item>> itemRegistrants = new HashMap<>();

    public static void init () {
        res.put(Type.OTHER, testRegistrants);
        res.put(Type.ENTITY, entityRegistrants);
        res.put(Type.BLOCK, blockRegistrants);
        res.put(Type.WALL, wallRegistrants);
        res.put(Type.ITEM, itemRegistrants);
        Log.print(TAG, "注册器组管理器初始化完成！");
    }
    /**
     * 获取指定命名空间的注册器，没有则新建一个
     * @param nameSpace 注册器的命名空间
     * @param clazzType 注册的类型
     * @return
     * @param <C>
     */
    public static <C extends ID> Registrant<C> getRegistrant (String nameSpace, Class<C> clazzType) {
        Type type = getRegistrantType(clazzType);

        HashMap map = res.get(type);
        if (map.get(nameSpace) == null) {
            newRegistrant(nameSpace, clazzType);
        }
        return (Registrant<C>) map.get(nameSpace);
    }

    /**
     * 获取注册器的类型
     * */
    private static <C> Type getRegistrantType (Class<C> clazzType) {
        if (clazzType.isAssignableFrom(Entity.class)) {
            return Type.ENTITY;
        }
        if (clazzType.isAssignableFrom(Block.class)) {
            return Type.BLOCK;
        }
        if (clazzType.isAssignableFrom(Wall.class)) {
            return Type.WALL;
        }
        if (clazzType.isAssignableFrom(Test.class)) {
            return Type.OTHER;
        }
        if (clazzType.isAssignableFrom(Item.class)) {
            return Type.ITEM;
        }
        return Type.OTHER;
    }

    /**
     * 新建一个注册器
     * */
    private static <C extends ID> void newRegistrant(String namespace, Class<C> clazzType) {
        Registrant<C> registrant = null;
        Type type = getRegistrantType(clazzType);
        if (clazzType.isAssignableFrom(Entity.class)) {
            registrant = new Registrant<C>(namespace);
        }
        if (clazzType.isAssignableFrom(Block.class)) {
            registrant = new Registrant<C>(namespace);
        }
        if (clazzType.isAssignableFrom(Wall.class)) {
            registrant = new Registrant<C>(namespace);
        }
        if (clazzType.isAssignableFrom(Item.class)) {
            registrant = new Registrant<C>(namespace);
        }
        if (clazzType.isAssignableFrom(Test.class)) {
            registrant = new Registrant<C>(namespace);
        }
        //先获取种类
        HashMap registrants = res.get(type);
        //再把对应命名空间的注册器添加进去
        registrants.put(namespace, registrant);
    }

    /**
     * 打印出游戏内所有注册的方块
     * */
    public static void printAllBlocks () {
        Array<String> allBlockName = new Array<>();
        for (Registrant<? extends Block> registrant : blockRegistrants.values()) {
            HashMap<String, ? extends Supplier<? extends Block>> r = registrant.getRegedit();
            for (String name : r.keySet()) {
                allBlockName.add(registrant.getId(name));
            }
        }
        printAll(allBlockName, "注册的方块有：");
    }

    /**
     * 打印出所有注册的实体
     */
    public static void printAllEntities () {
        Array<String> allName = new Array<>();
        for (Registrant<? extends Entity> registrant : entityRegistrants.values()) {
            HashMap<String, ? extends Supplier<? extends Entity>> r = registrant.getRegedit();
            for (String name : r.keySet()) {
                allName.add(registrant.getId(name));
            }
        }
        printAll(allName, "注册的实体有：");
    }
    /**
     * 打印出所有注册的物品
     */
    public static void printAllItems () {
        Array<String> allName = new Array<>();
        for (Registrant<? extends Item> registrant : itemRegistrants.values()) {
            HashMap<String, ? extends Supplier<? extends Item>> r = registrant.getRegedit();
            for (String name : r.keySet()) {
                allName.add(registrant.getId(name) + "::" + r.get(name).get().getClass().getName());
            }
        }
        printAll(allName, "注册的物品有：");
    }
    private static void printAll (Array<String> allName, String msg) {
        Log.print(TAG, msg);
        for (int i = 0; i < allName.size; i++) {
            if (i + 1 < allName.size) {
                System.out.print(allName.get(i) + " | ");
            }else {
                System.out.println(allName.get(i));
            }
        }
    }
}
