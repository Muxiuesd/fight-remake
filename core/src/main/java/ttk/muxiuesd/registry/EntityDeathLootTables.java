package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.loottable.common.LootEntry;
import ttk.muxiuesd.world.loottable.common.LootGroup;
import ttk.muxiuesd.world.loottable.entity.EntityDeathLootTable;

/**
 * 实体死亡战利品表的注册
 * */
public class EntityDeathLootTables {
    public static void init () {
        Log.print(EntityDeathLootTables.class.getName(), "实体死亡掉落战利品表注册完成");
    }

    /// 敌人实体
    public static final EntityDeathLootTable SLIME = register(Entities.SLIME,
        EntityDeathLootTable.Builder.create()
            .setGroups(
                LootGroup.of("material",
                    LootEntry.of(Items.SLIME_BALL, 1, 3, 1f)
                )
            )
            .build()
    );
    public static final EntityDeathLootTable ZOMBIE = register(Entities.ZOMBIE,
        EntityDeathLootTable.Builder.create()
            .setGroups(
                LootGroup.of("loot",
                    LootEntry.of(Items.RUBBISH, 1, 3, 50f),
                    LootEntry.of(Items.STICK, 1, 2, 49f),
                    LootEntry.of(Items.POTATO, 1, 2,  1f)
                )
            )
            .build()
    );

    /// 生物实体
    public static final EntityDeathLootTable PUFFER_FISH = register(Entities.PUFFER_FISH,
        EntityDeathLootTable.Builder.create()
            .setGroups(
                LootGroup.of("food",
                    LootEntry.of(Items.PUFFER_FISH, 1f)
                )
            )
            .build()
    );



    /**
     * 为实体注册对应的战利品表
     * @param entityProvider 实体的Provider
     * @param lootTable  对应的战利品表
     * */
    public static EntityDeathLootTable register (EntityProvider<?> entityProvider, EntityDeathLootTable lootTable) {
        return register(entityProvider.getIdentifier(), lootTable);
    }

    /**
     * 最基础的注册
     * @param identifier 实体的id标识符
     * @param lootTable  对应的战利品表
     * */
    public static EntityDeathLootTable register (Identifier identifier, EntityDeathLootTable lootTable) {
        return Registries.ENTITY_DEATH_LOOT_TABLE.register(identifier, lootTable);
    }
}
