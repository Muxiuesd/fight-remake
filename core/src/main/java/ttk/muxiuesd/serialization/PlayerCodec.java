package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * 玩家实体的编解码器
 * */
public class PlayerCodec extends JsonCodec<Player> {
    @Override
    public void encode (Player player, JsonDataWriter dataWriter) {
        //基础的活物实体编码
        Codecs.LIVING_ENTITY.encode(player, dataWriter);
    }

    @Override
    public Optional<Player> parse (JsonDataReader dataReader) {
        Player player = Entities.PLAYER.create(null);

        JsonValue propertyValue = dataReader.readObj("property");
        Optional<Entity.Property> propertyOptional = Codecs.ENTITY_PROPERTY.decode(
            new JsonDataReader(propertyValue)
        );
        propertyOptional.ifPresent(player::setProperty);
        //读取cat
        player.readCAT(propertyValue.get(Fight.ID("cat")));

        //读取背包数据
        JsonValue backpackValue = dataReader.readObj("backpack");
        Optional<Backpack> optionalBackpack = Codecs.BACKPACK.decode(new JsonDataReader(backpackValue));
        optionalBackpack.ifPresent(player::setBackpack);

        //读取装备背包数据
        JsonValue equipmentValue = dataReader.readObj("equipment");
        Optional<Backpack> optionalEquipment = Codecs.BACKPACK.decode(new JsonDataReader(equipmentValue));
        optionalEquipment.ifPresent(player::setEquipmentBackpack);

        //读取状态效果
        JsonValue buffs = dataReader.readObj("status_effect");
        Optional<LinkedHashMap<StatusEffect, StatusEffect.Data>> optionalEffectsMap = Codecs.STATUS_EFFECTS.decode(
            new JsonDataReader(buffs)
        );
        optionalEffectsMap.ifPresent(player::setEffects);

        return Optional.of(player);
    }
}
