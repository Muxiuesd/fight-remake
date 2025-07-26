package ttk.muxiuesd.serialization;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.JsonCodec;
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
            .writeFloat("x", wall.x)
            .writeFloat("y", wall.y);

        //记得调用一次cat写入
        wall.writeCAT(wall.getProperty().getCAT());
        //自定义属性
        dataWriter.objStart("property");
        wall.getProperty().getPropertiesMap().write(dataWriter);
        dataWriter.objEnd();
    }

    @Override
    protected Optional<Wall<?>> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        Wall<?> wall = Registries.WALL.get(id);
        Wall<?> self = wall.createSelf(new Vector2(dataReader.readFloat("x"), dataReader.readFloat("y")));
        self.setID(id);

        //属性解码
        JsonValue property = dataReader.readObj("property");
        for (JsonValue prop : property) {
            //读取每一个属性id，获取对应的属性，通过属性自己的读取来获取值
            String typeID = prop.name();
            PropertyType propertyType = Registries.PROPERTY_TYPE.get(typeID);
            self.getProperty().set(propertyType, propertyType.read(dataReader, typeID));
        }
        //读取cat
        self.readCAT(property.get(Fight.getId("cat")));

        return Optional.of(self);
    }
}
