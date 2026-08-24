package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.loottable.block.BlockDropLootTable;

/**
 * 方块物品
 * <p>
 * 贴图直接使用方块的贴图（id 与方块一致，通过已注册的 id→路径映射获取）
 * */
public class BlockItem extends ConsumptionItem {
    final Block block;

    public BlockItem(Block block) {
        this(block, new Property().setMaxCount(64));
    }

    public BlockItem(Block block, Property property) {
        super(property);
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
            //获取方块的掉落物表
            BlockDropLootTable dropLootTable = Registries.BLOCK_DROP_LOOT_TABLE.getOrNull(replacedBlock.getIdentifier());
            if (dropLootTable != null) {
                dropLootTable.generate(world, new BlockDropLootTable.Condition().setPos(worldPosition));
            }else {
                //找不到注册的战利品表就尝试找对应的方块物品
                Item item = Registries.ITEM.getOrNull(replacedBlock.getIdentifier());
                if (item != null) {
                    //把替换下来的方块变成方块物品并且变成物品实体形式掉落在世界上
                    ItemEntityGetter.get(user.getEntitySystem(), new ItemStack(item, 1))
                        .setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue())
                        .setPosition(worldPosition);
                }
            }
        }
        return true;
    }
}
