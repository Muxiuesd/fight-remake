package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.entity.abs.Entity;

import java.util.HashMap;


/**
 * 用于注册的实体类型
 * */
public class EntityType<T extends Entity<?>> {
    private Identifier identifier;
    //改实体类型的附属类型，比如玩家类型实体附属玩家子弹类型实体
    private final HashMap<Identifier, EntityType<?>> childTypes = new HashMap<>();

    /**
     * 创建该实体类型的数组用于实体系统管理
     * */
    public Array<T> createEntityArray () {
        return new Array<>();
    }

    /**
     * 添加附属类型
     * */
    public <E extends Entity<?>> EntityType<E> addChildType(String name, EntityType<E> childType) {
        this.childTypes.put(new Identifier(Fight.ID(name)), childType);
        return childType;
    }

    /**
     * 获取指定名称的附属类型
     * */
    public EntityType<?> getChildType (String name) {
        return this.childTypes.get(new Identifier(Fight.ID(name)));
    }

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public EntityType<T> setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public EntityType<T> setId (String id) {
        if (this.identifier == null) {
            this.identifier = new Identifier(id);
        } else {
            this.identifier.setID(id);
        }
        return this;
    }
}
