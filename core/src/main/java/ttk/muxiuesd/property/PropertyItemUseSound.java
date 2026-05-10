package ttk.muxiuesd.property;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Sounds;

/**
 * 物品属性：物品使用音效
 * */
public class PropertyItemUseSound extends PropertyType<AudioHolder>{
    @Override
    public void write (DataWriter<?> writer, AudioHolder data) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getId());
            jsonWriter.writeString("id", data.getID());
            jsonWriter.objEnd();
        }
    }

    @Override
    public AudioHolder read (DataReader<?> reader, String dataKey) {
        AudioHolder audioHolder = Sounds.ITEM_CLICK;
        if (reader instanceof JsonDataReader jsonReader) {
            JsonValue obj = jsonReader.readObj(dataKey);
            audioHolder = Registries.AUDIOS.get(obj.getString("id"));
        }
        return audioHolder;
    }
}
