package ttk.muxiuesd.property;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.world.block.BlockSoundsID;

public class PropertyBlockSoundsID extends PropertyType<BlockSoundsID>{
    @Override
    public void write (DataWriter<?> writer, BlockSoundsID data) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getId());
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.WALK), data.getID(BlockSoundsID.Type.WALK));
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.PUT), data.getID(BlockSoundsID.Type.PUT));
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.DESTROY), data.getID(BlockSoundsID.Type.DESTROY));
            jsonWriter.objEnd();
        }
    }

    @Override
    public BlockSoundsID read (DataReader<?> reader, String dataKey) {
        BlockSoundsID soundsID = new BlockSoundsID();
        if (reader instanceof JsonDataReader jsonReader) {
            JsonValue obj = jsonReader.readObj(dataKey);
            soundsID.setIds(new String[]{
                obj.getString(String.valueOf(BlockSoundsID.Type.WALK)),
                obj.getString(String.valueOf(BlockSoundsID.Type.PUT)),
                obj.getString(String.valueOf(BlockSoundsID.Type.DESTROY))
            });
        }
        return soundsID;
    }
}
