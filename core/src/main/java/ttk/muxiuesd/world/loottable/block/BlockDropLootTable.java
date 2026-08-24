package ttk.muxiuesd.world.loottable.block;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.block.BlockLootTable;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 方块的掉落战利品表
 * */
public class BlockDropLootTable implements BlockLootTable<BlockDropLootTable.Condition> {
    private ItemStack[] dropItems;

    /**
     * 根据条件生成掉落战利品
     * */
    @Override
    public void generate (World world, Condition condition) {
        //先简单的生成掉落物
        Vec2 pos = condition.getPos();
        ItemStack[] stacks = getDropItems();
        EntitySystem es = world.getSystem(EntitySystem.class);
        for (ItemStack itemStack : stacks) {
            //这里要用复制的，避免其他地方的修改污染战利品表的物品堆叠
            ItemStack copy = itemStack.copy();
            ItemEntityGetter.get(es, pos, copy)
                .setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
        }
    }

    public ItemStack[] getDropItems () {
        return this.dropItems;
    }

    public BlockDropLootTable setDropItems (ItemStack[] dropItems) {
        this.dropItems = dropItems;
        return this;
    }

    /**
     * 具体的条件类
     * */
    public static class Condition extends BlockLootTable.Condition {
        private Vec2 pos;    //方块被破坏的位置

        public Vec2 getPos () {
            return this.pos;
        }

        public Condition setPos (Vec2 pos) {
            this.pos = pos;
            return this;
        }

        public Condition setPos (Vector2 pos) {
            this.pos = new Vec2(pos);
            return this;
        }
    }

    /**
     * 构建器
     * */
    public static class Builder {
        private ItemStack[] dropItemStacks;


        /**
         * 创建
         * */
        public static Builder create () {
            return new Builder();
        }

        /**
         * 掉落方块自己的方块物品（需要对应的方块物品已注册）
         * */
        public Builder dropSelf (Block block) {
            Item blockItem = Registries.ITEM.get(block.getIdentifier());
            return this.setDropItems(blockItem);
        }

        /**
         * 添加指定的掉落物（快捷方法）
         * */
        public Builder setDropItems (Item... dropItems) {
            this.dropItemStacks = new ItemStack[dropItems.length];
            for (int i = 0; i < dropItems.length; i++) {
                this.dropItemStacks[i] = new ItemStack(dropItems[i], 1);
            }
            return this;
        }

        /**
         * 添加指定的掉落物的堆栈（可以指定一些属性之类的）
         * */
        public Builder setDropItems (ItemStack... dropItemStacks) {
            this.dropItemStacks = dropItemStacks;
            return this;
        }

        /**
         * 构建出来
         * */
        public BlockDropLootTable build () {
            return new BlockDropLootTable()
                .setDropItems(this.dropItemStacks);
        }
    }
}
