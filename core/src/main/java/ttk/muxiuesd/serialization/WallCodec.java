package ttk.muxiuesd.serialization;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.wall.Wall;

import java.util.Optional;

/**
 * 墙体编解码器
 * */
public class WallCodec extends JsonCodec<Wall<?>> {
    @Override
    public void encode (Wall<?> wall, JsonDataWriter dataWriter) {
        dataWriter
            .writeString("id", wall.getID())
            .writeString("codec_id", Fight.ID("wall"))
            .writeFloat("x", wall.x)
            .writeFloat("y", wall.y);

        //自定义属性
        dataWriter.objStart("property");
        Codecs.BLOCK_PROPERTY.encode (wall.getProperty(), dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<Wall<?>> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        Wall<?> wall = Registries.WALL.get(id);
        Wall<?> self = wall.createSelf(new Vector2(dataReader.readFloat("x"), dataReader.readFloat("y")));
        self.setIdentifier(Identifier.of(id));

        //属性解码
        JsonValue propertyValue = dataReader.readObj("property");
        Optional<Block.Property> propertyOptional = Codecs.BLOCK_PROPERTY.decode(new JsonDataReader(propertyValue));
        if (propertyOptional.isPresent()) {
            self.setProperty(propertyOptional.get());
        }

        return Optional.of(self);
    }
}
