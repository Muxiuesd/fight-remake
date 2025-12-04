package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.data.EntityDataOutput;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.EntityTask;

/**
 * 实体卸载任务
 * */
public class EntityUnloadTask extends EntityTask {
    private final Array<Entity<?>> entities;

    public EntityUnloadTask (EntitySystem entitySystem, Array<Entity<?>> entities, ChunkPosition chunkPosition) {
        super(entitySystem, chunkPosition);
        this.entities = entities;
    }

    @Override
    public Array<Entity<?>> call (){
        ChunkPosition chunkPosition = this.getChunkPosition();
        String chunkPosName = chunkPosition.toString();

        //TODO 修改实体保存的格式
        JsonDataWriter dataWriter = new JsonDataWriter();
        dataWriter.arrayStart();
        for (Entity<?> entity: this.getEntities()) {
            dataWriter.objStart();
            /*if (entity instanceof ItemEntity itemEntity) {
                Codecs.ITEM_ENTITY.encode(itemEntity, dataWriter);
            }else {
                Codecs.ENTITY.encode(entity, dataWriter);
            }*/

            //获取实体的编解码器来编码自己
            entity.getCodec().encode(entity, dataWriter);

            dataWriter.objEnd();
        }
        dataWriter.arrayEnd();

        EntityDataOutput entityDataOutput = new EntityDataOutput(chunkPosName);
        entityDataOutput.output(dataWriter);

        return this.getEntities();
    }

    /**
     * 获取需要被卸载的实体数组
     * */
    public Array<Entity<?>> getEntities () {
        return this.entities;
    }
}
