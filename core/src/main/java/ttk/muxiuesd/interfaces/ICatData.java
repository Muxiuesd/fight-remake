package ttk.muxiuesd.interfaces;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * cat数据的读写接口
 * */
public interface ICatData {
    void writeCatData (CatsHolder holder);
    void readCatData (JsonValue values);
}
