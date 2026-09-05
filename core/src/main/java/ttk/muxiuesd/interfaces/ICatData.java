package ttk.muxiuesd.interfaces;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.serialization.codecs.CodecCatsHolder;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * cat数据的读写接口
 * <p>
 * 写入统一走 {@link #writeCatData(CatsHolder)}；
 * 读取提供 {@link #readCatData(CatsHolder)}（推荐，类型化直读）与
 * {@link #readCatData(JsonValue)}（旧接口，桥接到 CatsHolder 版本）
 * */
public interface ICatData {
    void writeCatData (CatsHolder holder);

    /**
     * 从 cats 读取数据（推荐实现：直接使用 {@link CatsHolder} 类型化便捷读取）
     * <p>
     * 默认桥接到旧 {@link #readCatData(JsonValue)} 接口（转为 JsonValue 树），
     * 避免破坏现有实现
     * */
    default void readCatData (CatsHolder holder) {
        this.readCatData(CodecCatsHolder.toJsonValue(holder));
    }

    /**
     * 从 JsonValue 读取数据（旧接口，默认由 {@link #readCatData(CatsHolder)} 转换而来）
     * <p>
     * 默认空实现，使用旧接口的实现覆写此方法
     * */
    default void readCatData (JsonValue values) {
    }
}
