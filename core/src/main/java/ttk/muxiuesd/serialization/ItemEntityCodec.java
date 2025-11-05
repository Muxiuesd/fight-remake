package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.Optional;

/**
 * 物品实体的编解码器
 * */
public class ItemEntityCodec extends JsonCodec<ItemEntity> {
    @Override
    public void encode (ItemEntity itemEntity, JsonDataWriter dataWriter) {
        Codecs.ENTITY.encode(itemEntity, dataWriter);

        dataWriter.objStart("itemStack");
        Codecs.ITEM_STACK.encode(itemEntity.getItemStack(), dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<ItemEntity> parse (JsonDataReader dataReader) {
        ItemEntity itemEntity = null;
        Optional<Entity<?>> optionalEntity = Codecs.ENTITY.decode(dataReader);
        JsonValue itemStackValue = dataReader.readObj("itemStack");
        Optional<ItemStack> optionalItemStack = Codecs.ITEM_STACK.decode(new JsonDataReader(itemStackValue));
        if (optionalEntity.isPresent() && optionalItemStack.isPresent()) {
            itemEntity = (ItemEntity) optionalEntity.get();
            itemEntity.setItemStack(optionalItemStack.get());
        }
        return Optional.ofNullable(itemEntity);
    }
}
