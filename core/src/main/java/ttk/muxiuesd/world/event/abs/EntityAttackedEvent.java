package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.Entity;

public abstract class EntityAttackedEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((Entity) args[0], (Entity) args[1]);
    }

    /**
     * @param attackObject  造成攻击的实体，比如子弹
     * @param victim    受害的实体
     */
    public abstract void callback (Entity attackObject, Entity victim);
}
