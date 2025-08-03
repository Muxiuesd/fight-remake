package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.data.EntityDataOutput;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.EntityTask;

/**
 * 实体卸载任务
 * */
public class EntityUnloadTask extends EntityTask {
    private final ChunkPosition chunkPosition;

    public EntityUnloadTask (EntitySystem entitySystem, Array<Entity<?>> entities, ChunkPosition chunkPosition) {
        super(entitySystem, entities);
        this.chunkPosition = chunkPosition;
    }

    @Override
    public Array<Entity<?>> call (){
        ChunkPosition chunkPosition = this.getChunkPosition();
        String chunkPosName = chunkPosition.toString();

        //TODO 修改实体保存的格式
        JsonDataWriter dataWriter = new JsonDataWriter();
        dataWriter.arrayStart();
        for (Entity<?> entity: getEntities()) {
            dataWriter.objStart();
            Codecs.ENTITY.encode(entity, dataWriter);
            dataWriter.objEnd();
        }
        dataWriter.arrayEnd();

        EntityDataOutput entityDataOutput = new EntityDataOutput(chunkPosName);
        entityDataOutput.output(dataWriter);

        /*for (Entity<?> entity: getEntities()) {
            JsonDataWriter dataWriter = new JsonDataWriter();
            dataWriter.objStart();
            Codecs.ENTITY.encode(entity, dataWriter);
            dataWriter.objEnd();
            EntityDataOutput entityDataOutput = new EntityDataOutput(
                chunkPosName + "/" + entity.getClass().getSimpleName() + "@" + entity.hashCode()
            );
            entityDataOutput.output(dataWriter);
        }*/

        return getEntities();
    }

    public ChunkPosition getChunkPosition () {
        return this.chunkPosition;
    }
}
