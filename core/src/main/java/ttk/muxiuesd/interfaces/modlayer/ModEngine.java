package ttk.muxiuesd.interfaces.modlayer;

/**
 * Mod引擎
 * */
public interface ModEngine {
    /**
     * 游戏核心统一调用引擎初始化
     * */
    void init ();

    /**
     * 执行mod脚本代码
     * */
    ModEngine eval (String scriptCode);

    /**
     * 设置一个变量
     * */
    ModEngine put (String name, Object object);

    /**
     * 调用mod里面的某个函数或者方法
     * */
    Object invoke (String name, Object... args);
}
