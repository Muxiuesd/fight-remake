package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.ThreadUtils;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.AbsFileUtil;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.EntityTask;

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
        String json = AbsFileUtil.readFileAsString(Fight.getPathSaveEntities(), chunkPosName + ".json");
        RawObject root = RawObjectJsonConverter.fromJson(json);

        //对每一个实体数据值进行解析
        if (root.isList()) {
            for (Object item : root.asList().get()) {
                RawObject raw = Codec.wrap(item);

                try {
                    String id = Codec.STRING.decode(Codec.wrap(raw.asMap().get().get("id"))).result().orElse(null);
                    if (id == null) continue;

                    //获取实体的编解码器来解码数据变成类
                    EntityProvider<?> entityProvider = Registries.ENTITY.get(id);
                    Codec<Entity<?>> codec = (Codec<Entity<?>>) entityProvider.codec;
                    Entity<?> entity = codec.decode(raw).result().orElse(null);
                    if (entity != null) entities.add(entity);
                } catch (Exception ignored) {
                    ThreadUtils.yield();
                }
            }
        }
        //读取完成后删除文件
        AbsFileUtil.deleteFile(Fight.getPathSaveEntities(), chunkPosName + ".json");
        return entities;
    }
}
