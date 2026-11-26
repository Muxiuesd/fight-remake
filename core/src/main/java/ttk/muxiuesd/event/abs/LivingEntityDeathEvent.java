package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 活物实体死亡事件的抽象类
 * */
public abstract class LivingEntityDeathEvent implements Event {
    public abstract void handle (World world, LivingEntity<?> entity);
}
