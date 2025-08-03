package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.EntityTask;

import java.util.Optional;

/**
 * 实体加载任务
 * */
public class EntityLoadTask extends EntityTask {
    public EntityLoadTask (EntitySystem entitySystem, ChunkPosition chunkPosition) {
        super(entitySystem, chunkPosition);
    }

    @Override
    public Array<Entity<?>> call () throws Exception {
        Array<Entity<?>> entities = new Array<>();
        String chunkPosName = getChunkPosition().toString();
        JsonValue entitiesValue = FileUtil.readJsonFile(Fight.PATH_SAVE_ENTITIES, chunkPosName);

        //对每一个实体数据值进行解析
        for (JsonValue entityValue : entitiesValue) {
            JsonDataReader dataReader = new JsonDataReader(entityValue);
            Optional<Entity<?>> optionalEntity = Codecs.ENTITY.decode(dataReader);
            optionalEntity.ifPresent(entities::add);
        }

        return entities;
    }
}
