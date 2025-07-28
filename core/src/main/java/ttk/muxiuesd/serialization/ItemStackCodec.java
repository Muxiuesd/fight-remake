package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.Optional;

public class ItemStackCodec extends JsonCodec<ItemStack> {
    @Override
    public Optional<ItemStack> parse (JsonDataReader dataReader) {
        return Optional.empty();
    }

    @Override
    public void encode (ItemStack obj, JsonDataWriter dataWriter) {

    }
}
