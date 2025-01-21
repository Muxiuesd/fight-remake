package ttk.muxiuesd.registrant;

import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Entity;

import java.util.HashMap;

/**
 * 注册器组管理器，这个类管理着游戏内所有的注册器
 * */
public class RegistrantGroup {
    public static final int OTHER  = -1;
    public static final int ENTITY = 0;
    public static final int BLOCK  = 1;

    enum Type {
        OTHER, ENTITY, BLOCK
    }

    private static final HashMap<Type, HashMap> res = new HashMap<>();

    private static final HashMap<String, Registrant<? extends Test>> testRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Entity>> entityRegistrants = new HashMap<>();
    private static final HashMap<String, Registrant<? extends Block>> blockRegistrants = new HashMap<>();

    static {
        res.put(Type.OTHER, testRegistrants);
        res.put(Type.ENTITY, entityRegistrants);
        res.put(Type.BLOCK, blockRegistrants);
    }

    /**
     * 获取注册器，没有则新建一个
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
        if (clazzType.isAssignableFrom(Test.class)) {
            return Type.OTHER;
        }

        return Type.OTHER;
    }

    private static <C> void newRegistrant(String nameSpace, Class<C> clazzType) {
        Registrant registrant = null;
        Type type = getRegistrantType(clazzType);
        if (clazzType.isAssignableFrom(Entity.class)) {
            registrant = new Registrant<>(nameSpace, clazzType);
        }
        if (clazzType.isAssignableFrom(Block.class)) {
            registrant = new Registrant<>(nameSpace, clazzType);
        }
        if (clazzType.isAssignableFrom(Test.class)) {
            registrant = new Registrant<>(nameSpace, clazzType);
        }
        //先获取种类
        HashMap registrants = res.get(type);
        //再添加进去
        registrants.put(nameSpace, registrant);
    }

    public static void main(String[] args) {
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
