package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.Entity;

/**
 * 实体死亡事件
 * */
public abstract class EntityDeathEvent implements Event {
    public abstract void call (Entity deadEntity);
}
