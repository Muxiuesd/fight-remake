package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;

import java.util.Optional;

/**
 * 植物的编解码器
 * */
public class BotanyCodec extends JsonCodec<Botany> {
    @Override
    public void encode (Botany botany, JsonDataWriter dataWriter) {
        //基础属性（所有类型的方块必须写入）
        dataWriter
            .writeString("id", botany.getID())
            .writeString("codec_id", Fight.ID("botany"));

        //植物也是一个方块一个实例
        //记得调用一次cat写入
        botany.writeCatData(botany.getProperty().get(PropertyTypes.CATS));
        //编码自定义属性
        dataWriter.objStart("property");
        Codecs.BLOCK_PROPERTY.encode(botany.getProperty(), dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<Botany> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        Block block = Registries.BLOCK.get(id);
        //植物方块
        if (block instanceof Botany botany) {
            Botany self = botany.createSelf();

            //属性解码
            JsonValue propertyValue = dataReader.readObj("property");
            Optional<Block.Property> propertyOptional = Codecs.BLOCK_PROPERTY.decode(
                new JsonDataReader(propertyValue)
            );
            propertyOptional.ifPresent(self::setProperty);

            //读取cat
            self.readCatData(propertyValue.get(PropertyTypes.CATS.getId()));

            return Optional.of(self);
        }

        return Optional.empty();
    }
}
