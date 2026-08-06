package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import ttk.muxiuesd.data.EntityDataOutput;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.EntityTask;

import java.util.ArrayList;
import java.util.List;

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
        List<Object> entitiesList = new ArrayList<>();
        for (Entity<?> entity: this.getEntities()) {
            //获取实体的编解码器来编码自己
            EntityProvider<?> entityProvider = Registries.ENTITY.get(entity.getID());
            Codec<Entity<?>> codec = (Codec<Entity<?>>) entityProvider.codec;
            RawObject rawObject = codec.encode(entity);
            entitiesList.add(rawObject.unwrap());
        }

        JsonDataWriter dataWriter = new JsonDataWriter();
        RawObjectJsonConverter.toJson(dataWriter, RawObject.ofList(entitiesList));

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
