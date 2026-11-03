package ttk.muxiuesd.registry;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.loottable.common.LootEntry;
import ttk.muxiuesd.world.loottable.common.LootGroup;
import ttk.muxiuesd.world.loottable.fishing.FishingLootTable;

/**
 * 钓鱼战利品表注册
 * */
public class FishingLootTables {
    public static void init () {
        Log.print(FishingLootTables.class.getName(), "钓鱼战利品表注册完成");
    }

    /// 钓到鱼
    public static final FishingLootTable FISHING_FISH = register("fishing_fish", FishingLootTable.Builder.create()
        .setGroups(
            LootGroup.of("fish",
                LootEntry.of(Items.FISH, 1, 1, 20f),
                LootEntry.of(Items.PUFFER_FISH, 1, 1, 10f),
                LootEntry.of(Items.RUBBISH, 1, 1, 100f)
            )
        )
        .build()
    );

    /// 钓到药水
    public static final FishingLootTable FISHING_POTION = register("fishing_potion", FishingLootTable.Builder.create()
        .setGroups(
            LootGroup.of("potion",
                LootEntry.of(Items.POTION_HEAL_LEVEL_1, 1, 1, 10f),
                LootEntry.of(Items.POTION_HEAL_LEVEL_2, 1, 1, 1f),
                LootEntry.of(Items.POTION_POISON_LEVEL_1, 1, 1, 10f),
                LootEntry.of(Items.POTION_POISON_LEVEL_2, 1, 1, 1f),
                LootEntry.of(Items.RUBBISH, 1, 1, 100f)
            )
        )
        .build()
    );




    public static FishingLootTable register (String name, FishingLootTable lootTable) {
        return register(Identifier.of(Fight.ID(name)), lootTable);
    }


    /**
     * 最基础的钓鱼战利品表注册
     * */
    public static FishingLootTable register (Identifier identifier, FishingLootTable lootTable) {
        return Registries.FISHING_LOOT_TABLE.register(identifier, lootTable);
    }

    /**
     * 按注册的钓鱼表生成掉落
     * <p>
     * 没有登记钓鱼表就什么都不掉
     * @param hookPos    鱼钩位置，掉落物在这里生成
     * @param pullTarget 收杆目标位置，掉落物朝这里飞
     * @param pullSpeed  掉落物飞行的速度
     * @param luck       幸运值，0 表示不改变权重
     * */
    public static void generate (World world, LivingEntity<?> user, ItemStack fishingPole,
                                 Vector2 hookPos, Vector2 pullTarget, float pullSpeed, int luck) {
        //TODO 根据钓竿或者钓鱼位置的不同，选取不同的掉落物表来生成
        FishingLootTable lootTable = Registries.FISHING_LOOT_TABLE.getOrNull(Fight.ID("fishing_fish"));
        if (lootTable == null) return;
        lootTable.generate(
            world,
            new FishingLootTable.Conditions()
                .setHookPos(hookPos)
                .setPullTarget(pullTarget)
                .setPullSpeed(pullSpeed)
                .setLuck(luck)
        );
    }
}
