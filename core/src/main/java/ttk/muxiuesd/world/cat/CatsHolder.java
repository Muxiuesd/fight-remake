package ttk.muxiuesd.world.cat;

import ttk.muxiuesd.interfaces.ShallowCopyable;

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

    public HashMap<String, CatValue<?>> getMap () {
        return this.entries;
    }

    @Override
    public CatsHolder copy () {
        CatsHolder newInstance = new CatsHolder();
        this.getMap().forEach((key, catValue) -> {
            try {
                newInstance.put(key, catValue.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
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
