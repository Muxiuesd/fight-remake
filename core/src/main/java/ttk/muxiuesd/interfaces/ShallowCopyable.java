package ttk.muxiuesd.interfaces;

/**
 * 浅拷贝接口：将自己的所有值浅拷贝一份，生成一份新的对象并返回
 * */
public interface ShallowCopyable<T> {
    T copy();
}
