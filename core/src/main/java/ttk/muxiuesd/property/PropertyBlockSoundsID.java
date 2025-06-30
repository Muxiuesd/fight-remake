package ttk.muxiuesd.property;

import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.world.block.BlockSoundsID;

public class PropertyBlockSoundsID extends PropertyType<BlockSoundsID>{
    @Override
    public void write (DataWriter<?> writer, BlockSoundsID data) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getName());
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.WALK), data.getID(BlockSoundsID.Type.WALK));
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.PUT), data.getID(BlockSoundsID.Type.PUT));
            jsonWriter.writeString(String.valueOf(BlockSoundsID.Type.DESTROY), data.getID(BlockSoundsID.Type.DESTROY));
            jsonWriter.objEnd();
        }
    }
}
