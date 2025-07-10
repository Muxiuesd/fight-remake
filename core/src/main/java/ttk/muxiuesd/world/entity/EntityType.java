package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.world.entity.abs.Entity;


/**
 * 用于注册的实体类型
 * */
public class EntityType<T extends Entity> {

    /**
     * 创建该实体类型的数组用于实体系统管理
     * */
    public Array<T> createEntityArray () {
        return new Array<T>();
    }
}
