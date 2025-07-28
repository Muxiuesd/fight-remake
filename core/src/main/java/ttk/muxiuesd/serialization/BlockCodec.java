package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;

import java.util.Optional;

/**
 * 对于方块的编解码器
 * */
public class BlockCodec extends JsonCodec<Block> {
    @Override
    public void encode (Block block, JsonDataWriter dataWriter) {
        //基础属性（所有类型的方块必须写入）
        dataWriter.writeString("id", block.getID());

        //带有方块实体的方块是一个方块一个实例，所以需要写入自定义的各种属性
        if (block instanceof BlockWithEntity) {
            dataWriter.writeFloat("width", block.width)
            .writeFloat("height", block.height)
            .writeFloat("originX", block.originX)
            .writeFloat("originY", block.originY)
            .writeFloat("scaleX", block.scaleX)
            .writeFloat("scaleY", block.scaleY)
            .writeFloat("rotation", block.rotation);

            //记得调用一次cat写入
            block.writeCAT(block.getProperty().getCAT());
            //编码自定义属性
            dataWriter.objStart("property");
            Codecs.BLOCK_PROPERTY.encode(block.getProperty(), dataWriter);
            dataWriter.objEnd();
        }
    }

    @Override
    public Optional<Block> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        Block block = Registries.BLOCK.get(id);

        //对于有方块实体的方块
        if (block instanceof BlockWithEntity<?,?> blockWithEntity) {
            BlockWithEntity<?, ?> self = blockWithEntity.createSelf();
            //读取基础属性
            self.width = dataReader.readFloat("width");
            self.height = dataReader.readFloat("height");
            self.originX = dataReader.readFloat("originX");
            self.originY = dataReader.readFloat("originY");
            self.scaleX = dataReader.readFloat("scaleX");
            self.scaleY = dataReader.readFloat("scaleY");
            self.rotation = dataReader.readFloat("rotation");

            /*for (JsonValue prop : propertyValue) {
                //读取每一个属性id，获取对应的属性，通过属性自己的读取来获取值
                String typeID = prop.name();
                PropertyType propertyType = Registries.PROPERTY_TYPE.get(typeID);
                self.getProperty().set(propertyType, propertyType.read(dataReader, typeID));
            }*/
            //属性解码
            JsonValue propertyValue = dataReader.readObj("property");
            Optional<Block.Property> propertyOptional = Codecs.BLOCK_PROPERTY.decode(new JsonDataReader(propertyValue));
            if (propertyOptional.isPresent()) {
                self.setProperty(propertyOptional.get());
            }

            //读取cat
            self.readCAT(propertyValue.get(Fight.getId("cat")));

            return Optional.of(self);
        }

        //普通方块
        return Optional.of(block);
    }
}
