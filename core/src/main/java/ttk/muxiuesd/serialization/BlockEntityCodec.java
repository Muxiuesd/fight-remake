package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.Backpack;

import java.util.Optional;

/**
 * 方块实体的编解码器
 * */
public class BlockEntityCodec extends JsonCodec<BlockEntity> {
    @Override
    public void encode (BlockEntity blockEntity, JsonDataWriter dataWriter) {
        String id = blockEntity.getProvider().getID();
        dataWriter
            .writeString("id", id)
            .writeString("codec_id", Fight.ID("block_entity"));

        BlockPos blockPos = blockEntity.getBlockPos();
        Backpack backpack = blockEntity.getBackpack();
        dataWriter.objStart("blockPos")
            .writeFloat("x", blockPos.x)
            .writeFloat("y", blockPos.y)
            .objEnd();

        //编写容器信息
        dataWriter.objStart("inventory");
        Codecs.BACKPACK.encode(backpack, dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<BlockEntity> parse (JsonDataReader dataReader) {
        JsonValue blockPosValue = dataReader.readObj("blockPos");
        BlockPos blockPos = new BlockPos(
            blockPosValue.getFloat("x"),
            blockPosValue.getFloat("y")
        );
        String id = dataReader.readString("id");
        BlockEntityProvider<?> provider = Registries.BLOCK_ENTITY.get(id);
        BlockEntity blockEntity = provider.create(blockPos);
        blockEntity.setProvider(provider);
        //读取容器信息
        JsonValue inventoryValue = dataReader.readObj("inventory");
        Optional<Backpack> optionalBackpack = Codecs.BACKPACK.decode(new JsonDataReader(inventoryValue));
        optionalBackpack.ifPresent(blockEntity::setInventory);

        return Optional.of(blockEntity);
    }
}
