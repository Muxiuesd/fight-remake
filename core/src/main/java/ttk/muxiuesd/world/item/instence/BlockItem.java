package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 方块物品
 * */
public class BlockItem extends ConsumptionItem {
    final Block block;

    public BlockItem(Block block, String textureId) {
        this(block, new Property().setMaxCount(64), textureId);
    }

    public BlockItem(Block block, Property property, String textureId) {
        super(property, textureId);
        this.block = block;
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Vector2 worldPosition = Util.getMouseWorldPosition();
        ChunkSystem cs = world.getSystem(ChunkSystem.class);

        //替换鼠标点到的方块
        Block replacedBlock = cs.replaceBlock(this.block, worldPosition.x, worldPosition.y);
        //非空气方块才能掉落出来
        if (replacedBlock != Blocks.ARI) {
            //获取方块所对应的方块物品
            ItemStack stack = new ItemStack(Gets.ITEM(replacedBlock.getID()), 1);

            //把替换下来的方块变成方块物品并且变成物品实体形式掉落在世界上
            ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Fight.getId("item_entity"), user.getEntitySystem());
            itemEntity.setItemStack(stack);
            itemEntity.setPosition(worldPosition.x, worldPosition.y);
            itemEntity.setSize(replacedBlock.width / 2, replacedBlock.height / 2);
            itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
        }
        return true;
    }
}
