package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.async.ThreadUtils;
import game.muxiuesd.bedrockcore.app.interfaces.serialization.Codec;
import ttk.muxiuesd.Fight;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.AbsFileUtil;
import ttk.muxiuesd.util.ChunkPosition;
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
    public Array<Entity<?>> call (){
        Array<Entity<?>> entities = new Array<>();
        String chunkPosName = getChunkPosition().toString();
        JsonValue entitiesValue = AbsFileUtil.readJsonFile(Fight.getPathSaveEntities(), chunkPosName);

        //对每一个实体数据值进行解析
        for (JsonValue entityValue : entitiesValue) {
            JsonDataReader dataReader = new JsonDataReader(entityValue);
            String id = dataReader.readString("id");
            EntityProvider<?> entityProvider = Registries.ENTITY.get(id);

            try {
                //获取实体的编解码器来解码数据变成类
                Codec codec = entityProvider.codec;
                Optional<Entity<?>> optionalEntity = codec.decode(dataReader);
                optionalEntity.ifPresent(entities::add);
            } catch (Exception ignored) {
                ThreadUtils.yield();
            }
        }
        //读取完成后删除文件
        AbsFileUtil.deleteFile(Fight.getPathSaveEntities(), chunkPosName + ".json");
        return entities;
    }
}
