package ttk.muxiuesd.serialization;

import ttk.muxiuesd.Fight;
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
        dataWriter
            .writeString("codec_id", Fight.ID("backpack"))
            .writeInt("size", backpack.getSize());

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
        Backpack backpack = new Backpack(dataReader.readInt("size"));
        JsonDataReader itemStacksData = new JsonDataReader(dataReader.readObj("itemStacks"));
        for (int i = 0; i < backpack.getSize(); i++) {
            String index = String.valueOf(i);
            //该索引没有物品数据就跳过
            if (! itemStacksData.getParse().has(index)) continue;

            Optional<ItemStack> optional = Codecs.ITEM_STACK.decode(new JsonDataReader(itemStacksData.readObj(index)));
            if (optional.isPresent()) {
                backpack.setItemStack(i, optional.get());
            }
        }
        return Optional.of(backpack);
    }
}
