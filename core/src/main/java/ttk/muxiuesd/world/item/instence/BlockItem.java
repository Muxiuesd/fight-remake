package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.EntitiesReg;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 方块物品
 * */
public class BlockItem extends Item {
    final String blockId;

    public BlockItem(String blockId, String textureId) {
        this(blockId, new Property().setMaxCount(64), textureId);
    }

    public BlockItem(String blockId, Property property, String textureId) {
        super(Type.CONSUMPTION, property, textureId);
        this.blockId = blockId;
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        HandleInputSystem his = (HandleInputSystem) world.getSystemManager().getSystem("HandleInputSystem");
        Vector2 worldPosition = his.getMouseWorldPosition();
        ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");

        Block replacedBlock = cs.replaceBlock(Gets.block(blockId), worldPosition.x, worldPosition.y);

        ItemStack stack = new ItemStack(Gets.item(replacedBlock.getID()), 1);

        ItemEntity itemEntity = (ItemEntity) EntitiesReg.get("item_entity");
        itemEntity.setItemStack(stack);
        itemEntity.setPosition(worldPosition.x, worldPosition.y);
        itemEntity.setSize(replacedBlock.width / 2, replacedBlock.height / 2);
        user.getEntitySystem().add(itemEntity);

        return super.use(world, user);
    }
}
