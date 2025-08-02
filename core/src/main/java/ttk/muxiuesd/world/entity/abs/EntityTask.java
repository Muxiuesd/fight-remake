package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.system.EntitySystem;

import java.util.concurrent.Callable;

public abstract class EntityTask implements Callable<Array<Entity<?>>> {
    private final EntitySystem entitySystem;
    private final Array<Entity<?>> entities;


    public EntityTask (EntitySystem entitySystem, Array<Entity<?>> entities) {
        this.entitySystem = entitySystem;
        this.entities = entities;
    }

    public EntitySystem getEntitySystem () {
        return this.entitySystem;
    }

    public Array<Entity<?>> getEntities () {
        return this.entities;
    }
}
