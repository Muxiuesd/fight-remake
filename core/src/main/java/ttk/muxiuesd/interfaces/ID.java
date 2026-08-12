package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.id.Identifier;

/**
 * 所有拥有id的东西都要继承这个
 * <p>
 * Identifier 只在注册阶段由注册方法通过 {@link #setIdentifier} 给定，注册过后只能读取不能修改
 * */
public interface ID<T> {
    T setIdentifier(Identifier identifier);
    String getID();
}
