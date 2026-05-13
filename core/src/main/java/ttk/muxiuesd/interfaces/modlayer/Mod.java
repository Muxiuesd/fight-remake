package ttk.muxiuesd.interfaces.modlayer;

/**
 * Mod接口
 * */
public interface Mod {
    void preLoad ();
    void startRun ();
    void reload ();

    Mod put (String name, Object object);
    Object invoke (String name, Object... args);

    boolean isRunning();
    ModEngine getEngine ();
}
