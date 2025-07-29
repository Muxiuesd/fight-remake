package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.InventoryCodec;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.Optional;

/**
 * 实体背包容器的编解码器
 * */
public class BackpackCodec extends InventoryCodec<Backpack> {
    @Override
    public void encode (Backpack backpack, JsonDataWriter dataWriter) {
        dataWriter.writeInt("size", backpack.getSize());
        dataWriter.objStart("itemStacks");
        //背包为空直接结束
        if (backpack.isEmpty()) {
            dataWriter.objEnd();
            return;
        }

        for (int i = 0; i < backpack.getSize(); i++) {
            ItemStack itemStack = backpack.getItemStack(i);
            //索引下无物品直接跳过
            if (itemStack == null) continue;

            dataWriter.objStart(String.valueOf(i));
            Codecs.ITEM_STACK.encode(itemStack, dataWriter);
            dataWriter.objEnd();
        }
        dataWriter.objEnd();
    }

    @Override
    public Optional<Backpack> parse (JsonDataReader dataReader) {
        return Optional.empty();
    }
}
