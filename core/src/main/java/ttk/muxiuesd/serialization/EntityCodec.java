package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;

import java.util.Optional;

/**
 * 基础实体的编解码器
 * */
public class EntityCodec extends JsonCodec<Entity<?>> {
    @Override
    public void encode (Entity<?> entity, JsonDataWriter dataWriter) {
        dataWriter
            .writeString("id", entity.getID())
            .writeString("type", entity.getType().getId());

        dataWriter.objStart("property");
        //记得调用一次cat写入
        //entity.writeCAT(entity.getProperty().getCAT());
        entity.writeCatData(entity.getProperty().getCatsHolder());
        Codecs.ENTITY_PROPERTY.encode(entity.getProperty(), dataWriter);
        dataWriter.objEnd();

    }

    @Override
    public Optional<Entity<?>> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        EntityProvider<Entity<?>> entityProvider = (EntityProvider<Entity<?>>) Registries.ENTITY.get(id);
        String typeId = dataReader.readString("type");
        EntityType<Entity<?>> entityType = (EntityType<Entity<?>>) Registries.ENTITY_TYPE.get(typeId);
        Entity<?> entity = entityProvider.create(FightCore.getInstance().mainGameScreen.getWorld(), entityType);

        JsonValue propertyValue = dataReader.readObj("property");
        Optional<Entity.Property> propertyOptional = Codecs.ENTITY_PROPERTY.decode(
            new JsonDataReader(propertyValue)
        );
        propertyOptional.ifPresent(entity::setProperty);
        //读取cat
        entity.readCatData(propertyValue.get(PropertyTypes.CATS.getId()));
        //entity.readCAT(propertyValue.get(Fight.ID("cat")));


        return Optional.of(entity);
    }
}
