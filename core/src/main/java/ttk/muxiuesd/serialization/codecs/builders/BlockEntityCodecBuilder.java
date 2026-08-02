package ttk.muxiuesd.serialization.codecs.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder0;
import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder2;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.Backpack;

import java.util.function.Function;

/**
 * 方块实体的codec构建器
 * */
public class BlockEntityCodecBuilder {

    public static <T extends BlockEntity> Codec<T> create (
        Function<CodecBuilder2<T, BlockPos, String>, CodecBuilder2<T, BlockPos, String>> function
    ) {
        CodecBuilder0<T> builder0 = CodecBuilder.create();
        CodecBuilder2<T, BlockPos, String> builder2 = builder0
            .paramField("block_pos", BlockEntity::getBlockPos, BlockPos.CODEC)
            .paramField("id", (entity -> entity.getProvider().getID()), Codec.STRING)
            .field("inventory", BlockEntity::getInventory, BlockEntity::setInventory, Backpack.CODEC);

        return function.apply(builder2).factory((blockPos, id) -> {
            //从注册表获取这个种类的方块实体的提供者
            BlockEntityProvider<?> provider = Registries.BLOCK_ENTITY.get(id);
            //实例由提供者创建，同一种方块实体持有相同的提供者（相同的id）
            return (T) provider.create(blockPos);
        });
    }
}
