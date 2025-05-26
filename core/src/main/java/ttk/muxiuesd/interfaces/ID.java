package ttk.muxiuesd.interfaces;

/**
 * 所有拥有id的东西都要继承这个
 * */
public interface ID<T> {
    T setID(String id);
    String getID();
}
