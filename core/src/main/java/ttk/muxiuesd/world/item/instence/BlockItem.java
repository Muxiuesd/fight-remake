package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
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
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        HandleInputSystem his = (HandleInputSystem) world.getSystemManager().getSystem("HandleInputSystem");
        Vector2 worldPosition = his.getMouseWorldPosition();
        ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");
        //替换鼠标点到的方块
        Block replacedBlock = cs.replaceBlock(Gets.BLOCK(blockId), worldPosition.x, worldPosition.y);
        //获取方块所对应的方块物品
        ItemStack stack = new ItemStack(Gets.ITEM(replacedBlock.getID()), 1);

        //把替换下来的方块变成方块物品并且变成物品实体形式掉落在世界上
        ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Fight.getId("item_entity"), user.getEntitySystem());
        itemEntity.setItemStack(stack);
        itemEntity.setPosition(worldPosition.x, worldPosition.y);
        itemEntity.setSize(replacedBlock.width / 2, replacedBlock.height / 2);
        itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);

        return true;
    }
}
