package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.EntitiesReg;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 方块物品
 * */
public class BlockItem extends Item {
    final Supplier<Block> supplier;

    public BlockItem(Supplier<Block> supplier, String textureId) {
        this(supplier, new Property().setMaxCount(64), textureId);
    }

    public BlockItem(Supplier<Block> supplier, Property property, String textureId) {
        super(Type.CONSUMPTION, property, textureId);
        this.supplier = supplier;
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        HandleInputSystem his = (HandleInputSystem) world.getSystemManager().getSystem("HandleInputSystem");
        Vector2 worldPosition = his.getMouseWorldPosition();
        ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");

        Block replacedBlock = cs.replaceBlock(supplier.get(), worldPosition.x, worldPosition.y);

        String id = replacedBlock.getID();
        String[] split = id.split(":");
        Registrant<Item> itemReg = RegistrantGroup.getRegistrant(split[0], Item.class);
        Item item = itemReg.get(split[1]);
        ItemStack stack = new ItemStack(item);

        ItemEntity itemEntity = (ItemEntity) EntitiesReg.get("item_entity");
        itemEntity.setItemStack(stack);
        itemEntity.setPosition(worldPosition.x, worldPosition.y);

        user.getEntitySystem().add(itemEntity);

        return super.use(world, user);
    }
}
