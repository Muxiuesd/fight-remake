package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.Optional;

/**
 * 物品堆栈的编解码器
 * */
public class ItemStackCodec extends JsonCodec<ItemStack> {
    @Override
    public void encode (ItemStack itemStack, JsonDataWriter dataWriter) {
        dataWriter.writeString("id", itemStack.getItem().getID());
        dataWriter.writeInt("amount", itemStack.getAmount());
        Item.Property property = itemStack.getProperty();
        Codecs.ITEM_PROPERTY.encode(property, dataWriter);
    }

    @Override
    public Optional<ItemStack> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        int amount = dataReader.readInt("amount");
        Item item = Registries.ITEM.get(id);
        Optional<Item.Property> optional = Codecs.ITEM_PROPERTY.decode(dataReader);
        if (optional.isPresent()) {
            return Optional.of(new ItemStack(item, amount, optional.get().getPropertiesMap()));
        }
        return Optional.of(new ItemStack(item, amount));
    }
}
