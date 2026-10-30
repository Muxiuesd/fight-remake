package ttk.muxiuesd.world.loottable.block;

import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.block.BlockLootTable;
import ttk.muxiuesd.interfaces.world.loottable.LootTable;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.loottable.common.LootEntry;
import ttk.muxiuesd.world.loottable.common.LootGenerator;
import ttk.muxiuesd.world.loottable.common.LootGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 方块的掉落战利品表
 * <p>
 * 与实体死亡掉落同构：持有若干{@link LootGroup}，生成时先按
 * {@link ttk.muxiuesd.world.loottable.common.LootCondition} 过滤组，再交给{@link LootGenerator}抽取。
 * 组与组之间互相独立，{@code rollCount = 1} 时每个满足条件的组各出一次，
 * 所以"必掉 A 且必掉 B"要写成两个组，而不是一个组里的两个条目（同组是互斥的）
 * */
public class BlockDropLootTable implements BlockLootTable<BlockDropLootTable.Conditions> {
    /** 默认没有任何组：生成时什么都不掉 */
    private List<LootGroup> groups = Collections.emptyList();
    /** 每组抽取次数 */
    private int rollCount = 1;
    /** 同一条目能否在一次生成里重复掉落 */
    private boolean allowDuplicates = false;

    /**
     * 根据条件生成掉落战利品
     * */
    @Override
    public void generate (World world, Conditions conditions) {
        if (this.groups.isEmpty()) return;

        //先按条件过滤：不满足条件的组整组不参与抽取（例如作物未成熟时不查成熟组）
        boolean mature = conditions.isMature();
        List<LootGroup> availableGroups = new ArrayList<>(this.groups.size());
        for (LootGroup group : this.groups) {
            if (!group.isEmpty() && group.getCondition().test(mature)) {
                availableGroups.add(group);
            }
        }
        if (availableGroups.isEmpty()) return;

        List<ItemStack> drops = LootGenerator.generate(availableGroups, this.rollCount, this.allowDuplicates, 0);
        if (drops.isEmpty()) return;

        Vec2 pos = conditions.getPos();
        EntitySystem es = world.getSystem(EntitySystem.class);
        for (ItemStack drop : drops) {
            //数量被随机成 0 的条目会返回空堆叠，表示这一条目这次不掉
            if (drop.isVoid()) continue;

            //掉落物必须能被立即捡起来，否则会在地上躺很久
            ItemEntity itemEntity = ItemEntityGetter.getPickUpable(es, pos, drop);
            double radian = Util.randomRadian();
            float speed = Fight.getBlockDropItemEntityRandomSpeed();
            itemEntity.setVelocity(
                (float) Math.cos(radian),
                (float) Math.sin(radian)
            );
            itemEntity.setCurSpeed(speed);
        }
    }

    public List<LootGroup> getGroups () {
        return this.groups;
    }

    public BlockDropLootTable setGroups (LootGroup... groups) {
        this.groups = new ArrayList<>(Arrays.asList(groups));
        return this;
    }

    public int getRollCount () {
        return this.rollCount;
    }

    public BlockDropLootTable setRollCount (int rollCount) {
        this.rollCount = rollCount;
        return this;
    }

    public boolean isAllowDuplicates () {
        return this.allowDuplicates;
    }

    public BlockDropLootTable setAllowDuplicates (boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
        return this;
    }

    /**
     * 具体的条件类
     * */
    public static class Conditions extends LootTable.Conditions<Conditions> {
        /** 目标是否成熟，只有附着物（植物）用得到 */
        private boolean mature = false;

        public boolean isMature () {
            return this.mature;
        }

        public Conditions setMature (boolean mature) {
            this.mature = mature;
            return (Conditions) this;
        }
    }

    /**
     * 构建器
     * */
    public static class Builder {
        private final BlockDropLootTable table = new BlockDropLootTable();

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
         * 添加指定的掉落物（快捷方法），每个都必掉 1 个
         * */
        public Builder setDropItems (Item... dropItems) {
            ItemStack[] stacks = new ItemStack[dropItems.length];
            for (int i = 0; i < dropItems.length; i++) {
                stacks[i] = new ItemStack(dropItems[i], 1);
            }
            return this.setDropItems(stacks);
        }

        /**
         * 添加指定的掉落物的堆栈（可以指定一些属性之类的），每个都必掉它自己的数量
         * */
        public Builder setDropItems (ItemStack... dropItemStacks) {
            //每个掉落物单独成组：同组内是互斥的，放在同一组会变成"只掉其中一个"
            LootGroup[] groups = new LootGroup[dropItemStacks.length];
            for (int i = 0; i < dropItemStacks.length; i++) {
                groups[i] = LootGroup.of("drop_" + i, LootEntry.of(dropItemStacks[i], 1f));
            }
            return this.setGroups(groups);
        }

        /**
         * 直接指定若干组（需要条件、权重、随机数量时用这个）
         * */
        public Builder setGroups (LootGroup... groups) {
            this.table.groups = new ArrayList<>(Arrays.asList(groups));
            return this;
        }

        public Builder setRollCount (int rollCount) {
            this.table.rollCount = rollCount;
            return this;
        }

        public Builder setAllowDuplicates (boolean allowDuplicates) {
            this.table.allowDuplicates = allowDuplicates;
            return this;
        }

        /**
         * 构建出来
         * */
        public BlockDropLootTable build () {
            return this.table;
        }
    }
}
