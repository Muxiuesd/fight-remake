package ttk.muxiuesd.world.event;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.event.abs.LivingEntityDeathEvent;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.loottable.entity.EntityDeathLootTable;

/**
 * 活体生物死亡事件
 * */
public class EventLivingEntityDeath extends LivingEntityDeathEvent {
    @Override
    public void handle (World world, LivingEntity<?> entity) {
        entity.onDeath(world);

        //查找对应的死亡战利品表，战利品表生成战利品
        EntityDeathLootTable lootTable = Registries.ENTITY_DEATH_LOOT_TABLE.getOrNull(entity.getIdentifier());
        if (lootTable != null) {
            lootTable.generate(world, new EntityDeathLootTable.Conditions().setPos(entity.getPosition()));
        }

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.PLAYER_KILL, entity);
        Log.print(this.getClass().getName(), entity + " 死亡");
    }
}
