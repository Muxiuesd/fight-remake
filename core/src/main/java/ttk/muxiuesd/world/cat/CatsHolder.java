package ttk.muxiuesd.world.cat;

import game.muxiuesd.bedrockcore.app.interfaces.ShallowCopyable;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 自定义属性（custom attribute tag （CAT））标签持有者（算一个属性Property）
 * <p>
 * 持有管理一些自定义的属性
 * */
public class CatsHolder implements ShallowCopyable<CatsHolder> {
    private final HashMap<String, CatValue<?>> entries = new LinkedHashMap<>();

    /**
     * 添加一个cat
     * */
    public CatsHolder put (String key, CatValue<?> value) {
        this.getMap().put(key, value);
        return this;
    }

    /**
     * 获取一个cat
     * */
    public CatValue<?> get (String key) {
        return this.getMap().get(key);
    }

    /**
     * 类型化便捷读取（供 {@code readCatData} 直接使用，缺省返回默认值）
     * */
    public int getInt (String key, int def) {
        CatValue<?> v = this.get(key);
        return v instanceof CatInt ci ? ci.get() : def;
    }

    public float getFloat (String key, float def) {
        CatValue<?> v = this.get(key);
        return v instanceof CatFloat cf ? cf.get() : def;
    }

    public boolean getBoolean (String key, boolean def) {
        CatValue<?> v = this.get(key);
        return v instanceof CatBoolean cb ? cb.get() : def;
    }

    public String getString (String key, String def) {
        CatValue<?> v = this.get(key);
        return v instanceof CatString cs ? cs.get() : def;
    }

    public long getLong (String key, long def) {
        CatValue<?> v = this.get(key);
        return v instanceof CatLong cl ? cl.get() : def;
    }

    public HashMap<String, CatValue<?>> getMap () {
        return this.entries;
    }

    @Override
    public CatsHolder copy () {
        CatsHolder newInstance = new CatsHolder();
        this.getMap().forEach((key, catValue) -> {
            newInstance.put(key, catValue.clone());
        });
        return newInstance;
    }

    /**
     * 两个自定义属性标签持有者的判断是否相等
     * */
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        //boolean equals = false;
        if (obj instanceof CatsHolder otherHolder) {
            HashMap<String, CatValue<?>> thisMap = this.getMap();
            HashMap<String, CatValue<?>> otherMap = otherHolder.getMap();
            //持有的值的数量不同一定不同
            if (thisMap.size() != otherMap.size()) return false;

            for (String key : thisMap.keySet()) {
                CatValue<?> otherValue = otherMap.get(key);
                //找不到对应的值就是不相等
                if (otherValue == null) return false;

                //到这里就是有对应的值，就一个个比较值，有某个值的判断不相等就是持有者不相等
                if (!thisMap.get(key).equals(otherValue)) return false;
            }

            //到这里应该就是每一个cat值都相等
            return true;
        }
        return false;
    }
}
