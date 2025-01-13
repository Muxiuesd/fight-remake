package ttk.muxiuesd.registrant;

import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.enemy.Slime;

import java.util.HashMap;
import java.util.Objects;


public class RegistrantGroup {
    public static final int OTHER  = -1;
    public static final int ENTITY = 0;
    public static final int BLOCK  = 1;

    private static HashMap<Integer, HashMap> res = new HashMap<>();

    private static HashMap<String, Registrant<? extends Test>> testRegistrants = new HashMap();
    private static HashMap<String, Registrant<? extends Entity>> entityRegistrants = new HashMap();
    private static HashMap<String, Registrant<? extends Block>> blockRegistrants = new HashMap();

    static {
        res.put(OTHER, testRegistrants);
        res.put(ENTITY, entityRegistrants);
        res.put(BLOCK, blockRegistrants);
    }

    /**
     * 获取注册器，没有则新建一个
     * @param nameSpace 注册器的命名空间
     * @param clazzType 注册的类型
     * @return
     * @param <C>
     */
    public static <C> Registrant getRegistrant (String nameSpace, Class<C> clazzType) {
        int type = getRegistrantType(clazzType);

        HashMap map = res.get(type);
        if (map.get(nameSpace) == null) {
            newRegistrant(nameSpace, clazzType);
        }
        return (Registrant) map.get(nameSpace);
    }

    /**
     * 获取注册器的类型
     * */
    private static <C> int getRegistrantType (Class<C> clazzType) {
        if (clazzType.isAssignableFrom(Entity.class)) {
            return ENTITY;
        }
        if (clazzType.isAssignableFrom(Block.class)) {
            return BLOCK;
        }
        if (clazzType.isAssignableFrom(Test.class)) {
            return OTHER;
        }

        return OTHER;
    }

    private static <C> void newRegistrant(String nameSpace, Class<C> clazzType) {
        Registrant registrant = null;
        int type = getRegistrantType(clazzType);
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
        Registrant registrant = RegistrantGroup.getRegistrant("fight", Test.class);
        registrant.add("Water", new Test());
        registrant.add("Stone", new Test());
        Test water1 = (Test) registrant.get("Water");
        water1.id = "1";
        Test water2 = (Test) registrant.get("Water");
        water2.id = "2";
        System.out.println(water1);
        System.out.println(water2);
    }
}
