package ttk.muxiuesd.serialization.codecs;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder2;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 活物实体的现代化编解码器构造器
 * <p>
 * 在基础实体的字段之上又声明了活物实体的字段（生命值、背包、装备、状态效果等）
 */
public class LivingEntityCodecBuilder {
    /**
     * 背包的编解码器，把{@code Inventory}解码成{@link Backpack}
     * */
    private static final Codec<Backpack> BACKPACK_CODEC = Backpack.CODEC.xmap(
        inventory -> (Backpack) inventory,
        backpack -> backpack
    );

    /**
     * 创建一个活物实体的编解码器构造器
     * @param <T> 活物实体的类型
     * @return 带有两个构造参数（id、type）和基础实体、活物实体全部字段的构造器
     * */
    public static <T extends LivingEntity<?>> CodecBuilder2<T, String, String> create () {
        return EntityCodecBuilder.<T>create()
            .field("handIndex", LivingEntity::getHandIndex, LivingEntity::setHandIndex, Codec.INT)
            .field("maxHealth", LivingEntity::getMaxHealth, LivingEntity::setMaxHealth, Codec.FLOAT)
            .field("curHealth", LivingEntity::getCurHealth, LivingEntity::setCurHealth, Codec.FLOAT)
            .field("backpack", LivingEntity::getBackpack, LivingEntity::setBackpack, BACKPACK_CODEC)
            .field("equipment", LivingEntity::getEquipmentBackpack, LivingEntity::setEquipmentBackpack, BACKPACK_CODEC)
            .field("effects", LivingEntity::getEffects, LivingEntity::setEffects, CodecStatusEffects.CODEC);
    }
}
