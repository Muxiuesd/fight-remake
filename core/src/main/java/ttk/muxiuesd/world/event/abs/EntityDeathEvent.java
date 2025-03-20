package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.LivingEntity;

/**
 * 实体死亡事件
 * */
public abstract class EntityDeathEvent implements Event {
    public abstract void call (LivingEntity deadEntity);
}
