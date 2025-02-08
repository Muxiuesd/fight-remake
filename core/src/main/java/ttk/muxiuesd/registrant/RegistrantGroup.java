package ttk.muxiuesd.registrant;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.block.BlocksReg;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.wall.Wall;

import java.util.HashMap;

/**
 * 注册器组管理器，这个类管理着游戏内所有的注册器
 * */
public class RegistrantGroup {
    public static final String TAG = RegistrantGroup.class.getName();
    public static final int OTHER  = -1;
    public static final int ENTITY = 0;
    public static final int BLOCK  = 1;

    enum Type {
        OTHER, ENTITY, BLOCK, WALL
    }
    //总和
    private static final HashMap<Type, HashMap> res = new HashMap<>();

    private static final HashMap<String, Registrant<? extends Test>> testRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Entity>> entityRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Block>> blockRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Wall>> wallRegistrants = new HashMap<>();

    /*static {
        res.put(Type.OTHER, testRegistrants);
        res.put(Type.ENTITY, entityRegistrants);
        res.put(Type.BLOCK, blockRegistrants);
    }*/

    public RegistrantGroup () {
        res.put(Type.OTHER, testRegistrants);
        res.put(Type.ENTITY, entityRegistrants);
        res.put(Type.BLOCK, blockRegistrants);
        res.put(Type.WALL, wallRegistrants);
        Log.print(TAG, "注册器组管理器初始化完成！");
    }

    /**
     * 获取指定命名空间的注册器，没有则新建一个
     * @param nameSpace 注册器的命名空间
     * @param clazzType 注册的类型
     * @return
     * @param <C>
     */
    public static <C> Registrant<C> getRegistrant (String nameSpace, Class<C> clazzType) {
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

        return Type.OTHER;
    }

    /**
     * 新建一个注册器
     * */
    private static <C> void newRegistrant(String namespace, Class<C> clazzType) {
        Registrant registrant = null;
        Type type = getRegistrantType(clazzType);
        if (clazzType.isAssignableFrom(Entity.class)) {
            registrant = new Registrant<>(namespace, clazzType);
        }
        if (clazzType.isAssignableFrom(Block.class)) {
            registrant = new Registrant<>(namespace, clazzType);
        }
        if (clazzType.isAssignableFrom(Wall.class)) {
            registrant = new Registrant<>(namespace, clazzType);
        }
        if (clazzType.isAssignableFrom(Test.class)) {
            registrant = new Registrant<>(namespace, clazzType);
        }
        //先获取种类
        HashMap registrants = res.get(type);
        //再把对应命名空间的注册器添加进去
        registrants.put(namespace, registrant);
    }

    public static void printAllBlock () {
        Array<String> allBlockName = new Array<>();
        for (Registrant<? extends Block> registrant : blockRegistrants.values()) {
            HashMap<String, ? extends Block> r = registrant.getR();
            for (String id : r.keySet()) {
                allBlockName.add(id);
            }
        }
        Log.print(BlocksReg.class.getName(), "注册的方块有：");
        for (int i = 0; i < allBlockName.size; i++) {
            if (i + 1 < allBlockName.size) {
                System.out.print(allBlockName.get(i) + " | ");
            }else {
                System.out.println(allBlockName.get(i));
            }
        }
    }

    public static void main(String[] args) {
        new RegistrantGroup();
        Registrant<Test> registrant = RegistrantGroup.getRegistrant("fight", Test.class);
        registrant.register("Water", new TestObj1());
        registrant.register("Stone", new Test());
        TestObj1 water = (TestObj1) registrant.get("Water");
        water.id = "1";
        Test stone = (Test) registrant.get("Stone");
        stone.id = "2";
        System.out.println(water.id + " " + water.name);
        System.out.println(stone + stone.id);
    }
}
