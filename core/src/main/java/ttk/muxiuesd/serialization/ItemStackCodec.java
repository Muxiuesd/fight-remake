package ttk.muxiuesd.serialization;

import ttk.muxiuesd.Fight;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
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
        dataWriter
            .writeString("id", itemStack.getItem().getID())
            .writeString("codec_id", Fight.ID("item_stack"))
            .writeInt("amount", itemStack.getAmount());

        Item.Property property = itemStack.getProperty();
        Codecs.ITEM_PROPERTY.encode(property, dataWriter);
    }

    @Override
    public Optional<ItemStack> parse (JsonDataReader dataReader) {
        String id = dataReader.readString("id");
        int amount = dataReader.readInt("amount");
        Item item = Registries.ITEM.getOrNull(id);
        //未知物品 id（旧存档数据）：返回空，调用方（如背包）将槽位置空，不崩溃
        if (item == null) {
            return Optional.empty();
        }
        Optional<Item.Property> optional = Codecs.ITEM_PROPERTY.decode(dataReader);
        //属性读取正确就返回有对应属性的物品堆叠，没有就直接返回默认属性的物品堆叠
        return optional
            .map(property -> new ItemStack(item, amount, property))
            .or(() -> Optional.of(new ItemStack(item, amount)));
    }
}
