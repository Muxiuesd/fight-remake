package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.id.Identifier;

import java.util.HashMap;

/**
 * 注册接口
 * */
public interface Registry<T> {

    T register(Identifier identifier, T value);
    T get(Identifier identifier);
    T get(String id);
    boolean contains (Identifier identifier);
    boolean contains (String id);
    HashMap<Identifier, T> getMap ();
}
