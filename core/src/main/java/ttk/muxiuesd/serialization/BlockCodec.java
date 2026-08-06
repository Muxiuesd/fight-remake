package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;

import java.util.Optional;

/**
 * 对于方块的编解码器
 * */
public class BlockCodec extends JsonCodec<Block> {
    @Override
    public void encode (Block block, JsonDataWriter dataWriter) {
        //基础属性（所有类型的方块必须写入）
        dataWriter
            .writeString("id", block.getID())
            .writeString("codec_id", Fight.ID("block"));

        //带有方块实体的方块是一个方块一个实例，所以需要写入自定义的各种属性
        if (block instanceof BlockWithEntity blockWithEntity) {
            //记得调用一次cat写入
            blockWithEntity.writeCatData(blockWithEntity.getProperty().get(PropertyTypes.CATS));
            //编码自定义属性
            dataWriter.objStart("property");
            Codecs.BLOCK_PROPERTY.encode(blockWithEntity.getProperty(), dataWriter);
            dataWriter.objEnd();

            //写入方块实体信息
            dataWriter.objStart("block_entity");
            Codecs.BLOCK_ENTITY.encode(blockWithEntity.getBlockEntity(), dataWriter);
            dataWriter.objEnd();
        }
    }

    @Override
    public Optional<Block> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        Block block = Registries.BLOCK.get(id);

        //对于有方块实体的方块
        if (block instanceof BlockWithEntity blockWithEntity) {
            BlockWithEntity self = blockWithEntity.createSelf();

            //属性解码
            JsonValue propertyValue = dataReader.readObj("property");
            Optional<Block.Property> propertyOptional = Codecs.BLOCK_PROPERTY.decode(
                new JsonDataReader(propertyValue)
            );
            propertyOptional.ifPresent(self::setProperty);

            //读取方块实体信息
            JsonValue blockEntityValue = dataReader.readObj("block_entity");
            Optional<BlockEntity> optionalBlockEntity = Codecs.BLOCK_ENTITY.decode(new JsonDataReader(blockEntityValue));
            if (optionalBlockEntity.isPresent()) {
                BlockEntity blockEntity = optionalBlockEntity.get();
                self.setBlockEntity(blockEntity);
                blockEntity.setBlock(self);
            }
            //读取cat，同时会读取方块实体的cat
            self.readCatData(propertyValue.get(PropertyTypes.CATS.getId()));

            return Optional.of(self);
        }

        //普通方块
        return Optional.of(block);
    }
}
