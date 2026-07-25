package ttk.muxiuesd.property;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.BlockSounds;

/**
 * 方块音效集合的属性读写
 * */
public class PropertyBlockSounds extends PropertyType<BlockSounds>{
    @Override
    public void write (DataWriter<?> writer, BlockSounds data) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getId());
            /*jsonWriter.writeString(String.valueOf(BlockSounds.Type.WALK), data.getID(BlockSounds.Type.WALK));
            jsonWriter.writeString(String.valueOf(BlockSounds.Type.PUT), data.getID(BlockSounds.Type.PUT));
            jsonWriter.writeString(String.valueOf(BlockSounds.Type.DESTROY), data.getID(BlockSounds.Type.DESTROY));*/
            jsonWriter.writeString("id", data.getID());
            jsonWriter.objEnd();
        }
    }

    @Override
    public BlockSounds read (DataReader<?> reader, String dataKey) {
        BlockSounds sounds = BlockSounds.DEFAULT;
        if (reader instanceof JsonDataReader jsonReader) {
            JsonValue obj = jsonReader.readObj(dataKey);
            /*soundsID.setAudioHolders(new AudioHolder[]{
                obj.getString(String.valueOf(BlockSounds.Type.WALK)),
                obj.getString(String.valueOf(BlockSounds.Type.PUT)),
                obj.getString(String.valueOf(BlockSounds.Type.DESTROY))
            });*/
            sounds = Registries.BLOCK_SOUNDS.get(obj.getString("id"));
        }
        return sounds;
    }

    @Override
    public Codec<BlockSounds> getValueCodec () {
        return null;
    }

    @Override
    public Codec<PropertyType<BlockSounds>> getCodec () {
        return null;
    }
}
