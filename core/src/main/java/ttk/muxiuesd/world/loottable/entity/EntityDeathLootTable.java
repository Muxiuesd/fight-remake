package ttk.muxiuesd.world.loottable.entity;

import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.EntityLootTable;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 实体死亡的战利品表
 * */
public class EntityDeathLootTable implements EntityLootTable<EntityDeathLootTable.Conditions> {
    private ItemStack[] dropItems;

    @Override
    public void generate (World world, Conditions conditions) {
        //TODO 根据条件的复杂掉落生成机制

        //先简单的生成掉落物
        Vec2 pos = conditions.getPos();
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

    public EntityDeathLootTable setDropItems (ItemStack[] dropItems) {
        this.dropItems = dropItems;
        return this;
    }


    public static class Conditions extends EntityLootTable.Conditions<Conditions> {

    }

    /**
     * 构建器
     * */
    public static class Builder {
        private ItemStack[] dropItemStacks;


        /**
         * 创建
         * */
        public static EntityDeathLootTable.Builder create () {
            return new EntityDeathLootTable.Builder();
        }

        /**
         * 添加指定的掉落物（快捷方法）
         * */
        public EntityDeathLootTable.Builder setDropItems (Item... dropItems) {
            this.dropItemStacks = new ItemStack[dropItems.length];
            for (int i = 0; i < dropItems.length; i++) {
                this.dropItemStacks[i] = new ItemStack(dropItems[i], 1);
            }
            return this;
        }

        /**
         * 添加指定的掉落物的堆栈（可以指定一些属性与具体数量之类的）
         * */
        public EntityDeathLootTable.Builder setDropItems (ItemStack... dropItemStacks) {
            this.dropItemStacks = dropItemStacks;
            return this;
        }

        /**
         * 构建出来
         * */
        public EntityDeathLootTable build () {
            return new EntityDeathLootTable()
                .setDropItems(this.dropItemStacks);
        }
    }
}
