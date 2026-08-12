package ttk.muxiuesd.serialization.codecs.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder0;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.serialization.codecs.CodecCatsHolder;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.cat.CatsHolder;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 带有方块实体的方块的codec构建器
 * */
public class BlockWithEntityCodecBuilder {
    /**
     * 创建
     * @param function 传入builder，让用户自定义额外的字段
     * */
    public static <T extends BlockWithEntity> Codec<T> create (
        Supplier<T> supplier,
        Codec<? extends BlockEntity> blockEntityCodec,
        Function<CodecBuilder0<T>, CodecBuilder0<T>> function
    ) {
        CodecBuilder0<T> builder = CodecBuilder.create();
        builder
            .field("id",
                Block::getID,
                (block, id) -> {
                    //工厂创建出来的方块实例没有标识符，在这里恢复，否则getID()会空指针
                    if (id != null && Identifier.check(id)) {
                        block.setIdentifier(Identifier.of(id));
                    }
                },
                Codec.STRING)
            .field("block_entity",
                BlockWithEntity::getBlockEntity,
                BlockWithEntity::setBlockEntity,
                blockEntityCodec
            )
            .field("property",
                block -> {
                    //调用cats写入
                    CatsHolder cats = block.getProperty().get(PropertyTypes.CATS);
                    if (cats != null) {
                        block.writeCatData(cats);
                    }
                    return block.getProperty();
                },
                (block, property) -> {
                    //设置属性
                    block.setProperty(property);
                    //把属性中保存的cats数据读取到方块和方块实体上
                    CatsHolder cats = property.get(PropertyTypes.CATS);
                    if (cats != null) {
                        block.readCatData(CodecCatsHolder.toJsonValue(cats));
                    }
                },
                Block.Property.CODEC
            );

        return function.apply(builder).noArgFactory(supplier);
    }
}
