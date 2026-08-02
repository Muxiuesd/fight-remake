package ttk.muxiuesd.serialization.codecs.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder2;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 基础实体的现代化编解码器构造器
 * <p>
 * 声明了实体的id、type两个构造参数以及实体所有的基础字段，
 * 具体的实体类在此之上继续声明自己的字段，最后通过{@link #createEntity}来创建实例
 */
public class EntityCodecBuilder {
    /**
     * 创建一个基础实体的编解码器构造器
     * @param <T> 实体的类型
     * @return 带有两个构造参数（id、type）和全部基础字段的构造器
     * */
    public static <T extends Entity<?>> CodecBuilder2<T, String, String> create () {
        return CodecBuilder.<T>create()
            .paramField("id", Entity::getID, Codec.STRING)
            .paramField("type", e -> e.getType().getId(), Codec.STRING)
            .field("x", Entity::getX, Entity::setX, Codec.FLOAT)
            .field("y", Entity::getY, Entity::setY, Codec.FLOAT)
            .field("velX", Entity::getVelX, Entity::setVelX, Codec.FLOAT)
            .field("velY", Entity::getVelY, Entity::setVelY, Codec.FLOAT)
            .field("speed", Entity::getSpeed, Entity::setSpeed, Codec.FLOAT)
            .field("width", Entity::getWidth, Entity::setWidth, Codec.FLOAT)
            .field("height", Entity::getHeight, Entity::setHeight, Codec.FLOAT)
            .field("originX", Entity::getOriginX, Entity::setOriginX, Codec.FLOAT)
            .field("originY", Entity::getOriginY, Entity::setOriginY, Codec.FLOAT)
            .field("scaleX", Entity::getScaleX, Entity::setScaleX, Codec.FLOAT)
            .field("scaleY", Entity::getScaleY, Entity::setScaleY, Codec.FLOAT)
            .field("rotation", Entity::getRotation, Entity::setRotation, Codec.FLOAT)
            .field("onGround", Entity::isOnGround, Entity::setOnGround, Codec.BOOL);
    }

    /**
     * 根据实体的id和类型id来创建一个实体实例（通过实体注册表）
     * <p>
     * 解码时实体由注册表内的工厂创建，从而保证创建出来的是正确的实体类
     * */
    public static <T extends Entity<?>> T createEntity (String id, String typeId) {
        EntityProvider<?> entityProvider = Registries.ENTITY.get(id);
        EntityType<Entity<?>> entityType = (EntityType<Entity<?>>) Registries.ENTITY_TYPE.get(typeId);
        return (T) entityProvider.create(FightCore.getInstance().mainGameScreen.getWorld(), entityType);
    }
}
