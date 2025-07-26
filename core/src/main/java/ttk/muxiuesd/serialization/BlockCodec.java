package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;

import java.util.Optional;

/**
 * 对于方块的编解码器
 * */
public class BlockCodec extends JsonCodec<Block> {
    @Override
    public void encode (Block block, JsonDataWriter dataWriter) {
        //基础属性
        dataWriter
            .writeString("id", block.getID())
            .writeFloat("width", block.width)
            .writeFloat("height", block.height)
            .writeFloat("originX", block.originX)
            .writeFloat("originY", block.originY)
            .writeFloat("scaleX", block.scaleX)
            .writeFloat("scaleY", block.scaleY)
            .writeFloat("rotation", block.rotation);
        //自定义属性
        dataWriter.objStart("property");
        //记得调用一次cat写入
        block.writeCAT(block.getProperty().getCAT());
        block.getProperty().getPropertiesMap().write(dataWriter);
        dataWriter.objEnd();
    }

    @Override
    protected Optional<Block> parse (DataReader<JsonDataReader> dataReader) {
        return Optional.empty();
    }
}
