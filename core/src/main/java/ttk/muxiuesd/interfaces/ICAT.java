package ttk.muxiuesd.interfaces;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 自定义属性标签的行为接口
 * */
public interface ICAT {
    void writeCAT (CAT cat);
    void readCAT (JsonValue values);
}
