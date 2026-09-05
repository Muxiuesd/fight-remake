package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * cat数据的读写接口
 * <p>
 * 写入统一走 {@link #writeCatData(CatsHolder)}；
 * 读取统一走 {@link #readCatData(CatsHolder)}（与写入对称，类型化直读）
 * */
public interface ICatData {
    void writeCatData (CatsHolder holder);

    /**
     * 从 cats 读取数据（推荐实现：直接使用 {@link CatsHolder} 类型化便捷读取）
     * */
    void readCatData (CatsHolder holder);
}
