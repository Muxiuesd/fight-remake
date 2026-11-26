package ttk.muxiuesd.world.loottable.fishing;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.world.loottable.LootTable;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
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
 * 钓鱼战利品表
 * <p>
 * 和方块掉落表、实体死亡表一样走注册表：表由 {@code FishingLootTables} 注册，钓鱼时按 id 查表生成
 * <p>
 * 掉落物打捞上来的表现（朝使用者飞、落地自动回收）属于"钓鱼"这个场景，所以放在表里而不是物品里
 * */
public class FishingLootTable implements LootTable<FishingLootTable.Conditions> {
    private final List<LootGroup> groups;
    private int rollCount;              //每组抽取次数
    private boolean allowDuplicates;    //是否允许重复掉落

    private FishingLootTable (List<LootGroup> groups, int rollCount, boolean allowDuplicates) {
        this.groups = groups;
        this.rollCount = rollCount;
        this.allowDuplicates = allowDuplicates;
    }

    @Override
    public void generate (World world, Conditions conditions) {
        List<ItemStack> stacks = LootGenerator.generate(this.getGroups(), this.getRollCount(), this.isAllowDuplicates(), conditions.getLuck());

        Vector2 hookPos = conditions.getHookPos();
        Vector2 pullTarget = conditions.getPullTarget();
        EntitySystem es = world.getSystem(EntitySystem.class);
        for (ItemStack itemStack : stacks) {
            //跳过空堆叠（数量随机为 0 的条目不掉落）
            if (itemStack.isVoid()) continue;

            //钓上来的东西朝使用者飞过来
            ItemEntity itemEntity = ItemEntityGetter.getPickUpable(es, hookPos, itemStack)
                .setVelocity(new Direction(hookPos, pullTarget).toVector2())
                .setCurSpeed(conditions.getPullSpeed())
                .setOnGround(false);
            itemEntity.setOnAirTimer(
                Pools.TASK_TIMER.obtain()
                    .setMaxSpan(1.8f)
                    .setCurSpan(0)
                    .setTask(() -> {
                        Pools.TASK_TIMER.free(itemEntity.getOnAirTimer());
                        itemEntity.setOnAirTimer(null);
                    })
            );
        }
    }


    public static class Conditions extends LootTable.Conditions<Conditions> {
        private Vector2 hookPos;        //鱼钩位置，也是掉落物的生成位置
        private Vector2 pullTarget;     //收杆时的目标位置（使用者中心）
        private float pullSpeed;        //掉落物朝目标飞行的速度
        private int luck;               //幸运值

        public Vector2 getHookPos () {
            return hookPos;
        }

        public Conditions setHookPos (Vector2 hookPos) {
            this.hookPos = hookPos;
            return this;
        }

        public Vector2 getPullTarget () {
            return pullTarget;
        }

        public Conditions setPullTarget (Vector2 pullTarget) {
            this.pullTarget = pullTarget;
            return this;
        }

        public float getPullSpeed () {
            return pullSpeed;
        }

        public Conditions setPullSpeed (float pullSpeed) {
            this.pullSpeed = pullSpeed;
            return this;
        }

        public int getLuck () {
            return luck;
        }

        public Conditions setLuck (int luck) {
            this.luck = luck;
            return this;
        }
    }

    public List<LootGroup> getGroups () {
        return groups;
    }

    public int getRollCount () {
        return rollCount;
    }

    public FishingLootTable setRollCount (int rollCount) {
        this.rollCount = rollCount;
        return this;
    }

    public boolean isAllowDuplicates () {
        return allowDuplicates;
    }

    public FishingLootTable setAllowDuplicates (boolean allowDuplicates) {
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
        public static FishingLootTable.Builder create () {
            return new FishingLootTable.Builder();
        }

        private Builder () {
            this.groups = new ArrayList<>();
        }

        /**
         * 添加战利品组（组内条目互斥，按权重抽取）
         * */
        public FishingLootTable.Builder setGroups (LootGroup... groups) {
            this.groups.addAll(Arrays.asList(groups));
            return this;
        }

        /**
         * 设置每组抽取次数
         * */
        public FishingLootTable.Builder setRollCount (int rollCount) {
            this.rollCount = rollCount;
            return this;
        }

        /**
         * 设置是否允许同一条目在一次生成中重复掉落
         * */
        public FishingLootTable.Builder setAllowDuplicates (boolean allowDuplicates) {
            this.allowDuplicates = allowDuplicates;
            return this;
        }

        /**
         * 构建出来
         * */
        public FishingLootTable build () {
            return new FishingLootTable(this.groups, this.rollCount, this.allowDuplicates);
        }
    }
}
