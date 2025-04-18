package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 实体死亡事件
 * */
public abstract class EntityDeathEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((LivingEntity) args[0]);
    }
    public abstract void callback (LivingEntity deadEntity);
}
