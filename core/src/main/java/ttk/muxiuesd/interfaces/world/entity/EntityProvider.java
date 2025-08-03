package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体的提供类
 * */

public class EntityProvider<T extends Entity<?>> {
    private String id;

    private final Factory<T> factory;
    private final EntityType<? super T> defaultType;

    public final boolean canBeSaved;

    public EntityProvider (Factory<T> factory,
                           EntityType<? super T> defaultType,
                           boolean canBeSaved) {
        this.factory = factory;
        this.defaultType = defaultType;
        this.canBeSaved = canBeSaved;
    }

    /**
     * 新建一个实体实例
     * */
    public T create (World world) {
        return this.create(world, this.defaultType);
    }

    /**
     * 新建一个实体实例
     * */
    public T create (World world, EntityType<? super T> type) {
        T entity = factory.create(world, type);
        entity.setID(this.getId());
        return entity;
    }



    public String getId () {
        return this.id;
    }

    public EntityProvider<T> setId (String id) {
        this.id = id;
        return this;
    }

    public static class Builder<T extends Entity<?>> {
        final Factory<T> factory;
        EntityType<? super T> defaultType;

        boolean canBeSaved  = true; //实体能否被保存，默认可以

        private Builder (Factory<T> factory) {
            this.factory = factory;
        }

        public static <T extends Entity<?>> Builder<T> create (Factory<T> factory) {
            return new Builder<>(factory);
        }

        public Builder<T> setDefaultType (EntityType<? super T> defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        public Builder<T> setCanBeSaved (boolean canBeSaved) {
            this.canBeSaved = canBeSaved;
            return this;
        }

        public EntityProvider<T> build () {
            return new EntityProvider<T>(
                factory,
                defaultType,
                canBeSaved
            );
        }
    }

    @FunctionalInterface
    public interface Factory<T extends Entity<?>> {
        T create (World world, EntityType<? super T> entityType);
    }
}
