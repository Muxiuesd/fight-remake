package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.ChunkPosition;

import java.util.concurrent.Callable;

public abstract class EntityTask implements Callable<Array<Entity<?>>> {
    private final EntitySystem entitySystem;
    private final ChunkPosition chunkPosition;

    public EntityTask (EntitySystem entitySystem, ChunkPosition chunkPosition) {
        this.entitySystem = entitySystem;
        this.chunkPosition = chunkPosition;
    }

    public EntitySystem getEntitySystem () {
        return this.entitySystem;
    }

    public ChunkPosition getChunkPosition () {
        return this.chunkPosition;
    }
}
