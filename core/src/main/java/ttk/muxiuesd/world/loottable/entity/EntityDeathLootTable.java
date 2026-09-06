package ttk.muxiuesd.world.loottable.entity;

import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.interfaces.world.entity.EntityLootTable;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.loottable.common.LootGenerator;
import ttk.muxiuesd.world.loottable.common.LootGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 实体死亡的战利品表
 * */
public class EntityDeathLootTable implements EntityLootTable<EntityDeathLootTable.Conditions> {
    private final List<LootGroup> groups;
    private int rollCount;              //每组抽取次数
    private boolean allowDuplicates;    //是否允许重复掉落

    private EntityDeathLootTable (List<LootGroup> groups, int rollCount, boolean allowDuplicates) {
        this.groups = groups;
        this.rollCount = rollCount;
        this.allowDuplicates = allowDuplicates;
    }

    @Override
    public void generate (World world, Conditions conditions) {
        //根据组列表加权随机抽取掉落物，每组独立抽取（幸运值待接入）
        List<ItemStack> stacks = LootGenerator.generate(this.getGroups(), this.getRollCount(), this.isAllowDuplicates(), 0);

        Vec2 pos = conditions.getPos();
        EntitySystem es = world.getSystem(EntitySystem.class);
        for (ItemStack itemStack : stacks) {
            ItemEntity itemEntity = ItemEntityGetter.getPickUpable(es, pos, itemStack);
            itemEntity
                .setSpeed(0f)
                .setOnGround(false)
                .setOnAirTimer(
                    Pools.TASK_TIMER.obtain().setMaxSpan(0.5f).setCurSpan(0)
                    .setTask(() -> {
                        Pools.TASK_TIMER.free(itemEntity.getOnAirTimer());
                        itemEntity.setOnAirTimer(null);
                    })
                );
            //随机速度
            Util.entityRandomVelocity(itemEntity, 0.3f, 1.1f);
        }
    }


    public static class Conditions extends EntityLootTable.Conditions<Conditions> {

    }

    public List<LootGroup> getGroups () {
        return groups;
    }

    public int getRollCount () {
        return rollCount;
    }

    public EntityDeathLootTable setRollCount (int rollCount) {
        this.rollCount = rollCount;
        return this;
    }

    public boolean isAllowDuplicates () {
        return allowDuplicates;
    }

    public EntityDeathLootTable setAllowDuplicates (boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
        return this;
    }

    /**
     * 构建器
     * */
    public static class Builder {
        private final List<LootGroup> groups;
        private int rollCount = 1;              // 每组抽取次数，默认 1
        private boolean allowDuplicates = false;// 是否允许重复掉落，默认不允许


        /**
         * 创建
         * */
        public static EntityDeathLootTable.Builder create () {
            return new EntityDeathLootTable.Builder();
        }

        private Builder () {
            this.groups = new ArrayList<>();
        }

        /**
         * 添加战利品组（组内条目互斥，按权重抽取）
         * */
        public EntityDeathLootTable.Builder setGroups (LootGroup... groups) {
            this.groups.addAll(Arrays.asList(groups));
            return this;
        }

        /**
         * 设置每组抽取次数
         * */
        public EntityDeathLootTable.Builder setRollCount (int rollCount) {
            this.rollCount = rollCount;
            return this;
        }

        /**
         * 设置是否允许同一条目在一次生成中重复掉落
         * */
        public EntityDeathLootTable.Builder setAllowDuplicates (boolean allowDuplicates) {
            this.allowDuplicates = allowDuplicates;
            return this;
        }

        /**
         * 构建出来
         * */
        public EntityDeathLootTable build () {
            return new EntityDeathLootTable(this.groups, this.rollCount, this.allowDuplicates);
        }
    }
}
