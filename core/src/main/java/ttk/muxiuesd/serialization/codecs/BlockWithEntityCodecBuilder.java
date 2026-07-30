package ttk.muxiuesd.serialization.codecs;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder0;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;

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
        Codec<BlockEntity> blockEntityCodec,
        Function<CodecBuilder0<T>, CodecBuilder0<T>> function
    ) {
        CodecBuilder0<T> builder = CodecBuilder.create();
        builder
            .field("id", Block::getID, (a, b) -> {}, Codec.STRING)
            .field("block_entity",
                BlockWithEntity::getBlockEntity,
                BlockWithEntity::setBlockEntity,
                blockEntityCodec
            )
            .field("property",
                block -> {
                    //调用cats写入
                    block.writeCatData(block.getProperty().get(PropertyTypes.CATS));
                    return block.getProperty();
                },
                (block, property) -> {
                    //读取cat，同时会读取方块实体的cat
                    //block.readCatData(property.get(PropertyTypes.CATS));
                },
                Block.Property.CODEC
            );

        return function.apply(builder).noArgFactory(supplier);
    }
}
