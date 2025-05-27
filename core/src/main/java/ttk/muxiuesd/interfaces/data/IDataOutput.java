package ttk.muxiuesd.interfaces.data;

public interface IDataOutput<T extends DataWriter<?>> {
    void output (T writer);
}
