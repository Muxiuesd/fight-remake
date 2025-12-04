package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.id.Identifier;

import java.util.HashMap;

/**
 * 注册接口
 * */
public interface Registry<T> {
    /**
     * 注册
     * */
    T register(Identifier identifier, T value);

    /**
     * 根据id类获取
     * */
    T get(Identifier identifier);

    /**
     * 根据字符串id获取
     * */
    T get(String id);

    /**
     * 根据id类检测是否包含
     * */
    boolean contains (Identifier identifier);
    /**
     * 根据字符串id检测是否包含
     * */
    boolean contains (String id);

    /**
     * 获取map
     * */
    HashMap<Identifier, T> getMap ();
}
