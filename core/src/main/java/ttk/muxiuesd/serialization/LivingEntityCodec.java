package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * 活物实体的编解码器
 * */
public class LivingEntityCodec extends JsonCodec<LivingEntity<?>> {

    @Override
    public void encode (LivingEntity<?> livingEntity, JsonDataWriter dataWriter) {
        /*dataWriter
            .writeString("id", livingEntity.getID())
            .writeString("type", livingEntity.getType().getId());

        dataWriter.objStart("property");
        //记得调用一次cat写入
        livingEntity.writeCAT(livingEntity.getProperty().getCAT());
        Codecs.ENTITY_PROPERTY.encode(livingEntity.getProperty(), dataWriter);
        dataWriter.objEnd();*/
        Codecs.ENTITY.encode(livingEntity, dataWriter);

        //编码背包数据
        dataWriter.objStart("backpack");
        Codecs.BACKPACK.encode(livingEntity.getBackpack(), dataWriter);
        dataWriter.objEnd();

        //编码装备背包数据
        dataWriter.objStart("equipment");
        Codecs.BACKPACK.encode(livingEntity.getEquipmentBackpack(), dataWriter);
        dataWriter.objEnd();

        //编码状态效果
        dataWriter.objStart("status_effect");
        Codecs.STATUS_EFFECTS.encode(livingEntity.getEffects(), dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<LivingEntity<?>> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        EntityProvider<LivingEntity<?>> entityProvider = (EntityProvider<LivingEntity<?>>) Registries.ENTITY.get(id);

        String typeId = dataReader.readString("type");
        EntityType<LivingEntity<?>> entityType = (EntityType<LivingEntity<?>>) Registries.ENTITY_TYPE.get(typeId);
        LivingEntity<?> livingEntity = entityProvider.create(null, entityType);

        JsonValue propertyValue = dataReader.readObj("property");
        Optional<Entity.Property> propertyOptional = Codecs.ENTITY_PROPERTY.decode(
            new JsonDataReader(propertyValue)
        );
        propertyOptional.ifPresent(livingEntity::setProperty);

        //读取cat
        livingEntity.readCAT(propertyValue.get(Fight.ID("cat")));

        //读取背包数据
        JsonValue backpackValue = dataReader.readObj("backpack");
        Optional<Backpack> optionalBackpack = Codecs.BACKPACK.decode(new JsonDataReader(backpackValue));
        optionalBackpack.ifPresent(livingEntity::setBackpack);

        //读取装备背包数据
        JsonValue equipmentValue = dataReader.readObj("equipment");
        Optional<Backpack> optionalEquipment = Codecs.BACKPACK.decode(new JsonDataReader(equipmentValue));
        optionalEquipment.ifPresent(livingEntity::setEquipmentBackpack);

        //读取状态效果
        JsonValue buffs = dataReader.readObj("status_effect");
        Optional<LinkedHashMap<StatusEffect, StatusEffect.Data>> optionalEffectsMap = Codecs.STATUS_EFFECTS.decode(
            new JsonDataReader(buffs)
        );
        optionalEffectsMap.ifPresent(livingEntity::setEffects);

        return Optional.of(livingEntity);
    }
}
