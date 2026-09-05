package ttk.muxiuesd.serialization.codecs.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder2;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.codecs.CodecCatsHolder;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatsHolder;
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
            .field("onGround", Entity::isOnGround, Entity::setOnGround, Codec.BOOL)
            //cats 自定义数据：编码时先把实体字段写入 cats，解码时恢复（缺失字段时跳过，兼容旧存档）
            .field("cats",
                entity -> {
                    CatsHolder cats = entity.getProperty().getCatsHolder();
                    //防御：property 异常路径可能没有 CATS 属性
                    if (cats == null) cats = new CatsHolder();
                    entity.writeCatData(cats);
                    return cats;
                },
                (entity, cats) -> {
                    if (cats != null) {
                        entity.getProperty().setCatsHolder(cats);
                        entity.readCatData(cats);
                    }
                },
                CodecCatsHolder.CODEC);
    }

    /**
     * 根据实体的id和类型id来创建一个实体实例（通过实体注册表）
     * <p>
     * 解码时实体由注册表内的工厂创建，从而保证创建出来的是正确的实体类
     * */
    public static <T extends Entity<?>> T createEntity (String id, String typeId) {
        EntityProvider<?> entityProvider = Registries.ENTITY.getOrNull(id);
        if (entityProvider == null) {
            throw new IllegalArgumentException("实体注册表中不存在id为：" + id + " 的实体（旧存档数据）");
        }
        EntityType<Entity<?>> entityType = (EntityType<Entity<?>>) Registries.ENTITY_TYPE.getOrNull(typeId);
        if (entityType == null) {
            throw new IllegalArgumentException("实体类型注册表中不存在id为：" + typeId + " 的实体类型（旧存档数据）");
        }
        World world = FightCore.getInstance().mainGameScreen == null
            ? null
            : FightCore.getInstance().mainGameScreen.getWorld();
        if (world == null) {
            throw new IllegalStateException("当前没有可用的世界（实体必须在游戏世界中加载）");
        }
        return (T) entityProvider.create(world, entityType);
    }
}
