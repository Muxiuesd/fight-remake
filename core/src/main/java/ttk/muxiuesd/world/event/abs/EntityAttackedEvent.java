package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.Entity;

public abstract class EntityAttackedEvent implements Event {
    /**
     * @param attackObject 造成攻击的实体，比如子弹
     * @param victim    受害的实体
     */
    public abstract void call (Entity attackObject, Entity victim);
}
