package ttk.muxiuesd.event;

import ttk.muxiuesd.event.abs.BulletShootEvent;
import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.event.poster.EventPosterBulletShoot;
import ttk.muxiuesd.event.poster.EventPosterEntityHurt;

/**
 * 所有类型的事件
 * */
public class EventTypes {
    public static final String BULLET_SHOOT = EventBus.register("BulletShoot",
        new EventHandler<BulletShootEvent, EventPosterBulletShoot>() {
        @Override
        public void callEvents (EventPosterBulletShoot poster) {
            getEvents().forEach(event -> event.handle(poster.shooter, poster.bullet));
        }
    });

    public static final String ENTITY_HURT = EventBus.register("EntityHurt",
        new EventHandler<EntityHurtEvent, EventPosterEntityHurt>() {
        @Override
        public void callEvents (EventPosterEntityHurt poster) {
            getEvents().forEach(event -> event.handle(poster.attackObject, poster.victim));
        }
    });
}
