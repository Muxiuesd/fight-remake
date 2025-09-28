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

import java.util.Objects;
import java.util.Optional;

/**
 * 实体加载任务
 * */
public class EntityLoadTask extends EntityTask {
    public EntityLoadTask (EntitySystem entitySystem, ChunkPosition chunkPosition) {
        super(entitySystem, chunkPosition);
    }

    @Override
    public Array<Entity<?>> call (){
        Array<Entity<?>> entities = new Array<>();
        String chunkPosName = getChunkPosition().toString();
        JsonValue entitiesValue = FileUtil.readJsonFile(Fight.PATH_SAVE_ENTITIES, chunkPosName);

        //对每一个实体数据值进行解析
        for (JsonValue entityValue : entitiesValue) {
            JsonDataReader dataReader = new JsonDataReader(entityValue);
            String id = dataReader.readString("id");
            if (Objects.equals(id, Fight.ID("item_entity"))) {
                Optional<ItemEntity> optionalItemEntity = Codecs.ITEM_ENTITY.decode(dataReader);
                optionalItemEntity.ifPresent(entities::add);
            }else {
                Optional<Entity<?>> optionalEntity = Codecs.ENTITY.decode(dataReader);
                optionalEntity.ifPresent(entities::add);
            }
        }
        //读取完成后删除文件
        FileUtil.deleteFile(Fight.PATH_SAVE_ENTITIES, chunkPosName + ".json");
        return entities;
    }
}
