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
     * 安全获取：id 未注册时返回 null 而不是抛异常
     * <p>
     * 用于存档解码等容错路径（未知 id 的旧数据应跳过而不是崩溃/整块重建）
     * */
    default T getOrNull (Identifier identifier) {
        return this.getOrNull(identifier.getID());
    }

    /**
     * 安全获取：id 未注册时返回 null 而不是抛异常（存档解码等容错路径使用）
     * */
    default T getOrNull(String id) {
        return null;
    }

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
