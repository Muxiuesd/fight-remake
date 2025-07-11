package ttk.muxiuesd.event;

import ttk.muxiuesd.event.abs.*;
import ttk.muxiuesd.event.poster.*;
import ttk.muxiuesd.util.Log;

/**
 * 所有类型的事件
 * */
public class EventTypes {
    public static void init (){
        Log.print(EventTypes.class.getName(), "所有游戏事件注册完毕");
    }

    public static final String BULLET_SHOOT = EventBus.register("BulletShoot",
        new EventHandler<BulletShootEvent, EventPosterBulletShoot>() {
        @Override
        public void callEvents (EventPosterBulletShoot poster) {
            getEvents().forEach(event -> event.handle(poster.world, poster.shooter, poster.bullet));
        }
    });

    public static final String ENTITY_HURT = EventBus.register("EntityHurt",
        new EventHandler<EntityHurtEvent, EventPosterEntityHurt>() {
        @Override
        public void callEvents (EventPosterEntityHurt poster) {
            getEvents().forEach(event -> event.handle(poster.world, poster.attackObject, poster.victim));
        }
    });

    public static final String ENTITY_DEATH = EventBus.register("EntityDeath",
        new EventHandler<LivingEntityDeathEvent, EventPosterEntityDeath>() {
        @Override
        public void callEvents (EventPosterEntityDeath poster) {
            getEvents().forEach(event -> event.handle(poster.world, poster.entity));
        }
    });

    public static final String PLAYER_DEATH = EventBus.register("PlayerDeath",
        new EventHandler<PlayerDeathEvent, EventPosterPlayerDeath>() {
        @Override
        public void callEvents (EventPosterPlayerDeath poster) {
            getEvents().forEach(event -> event.handle(poster.world, poster.player));
        }
    });

    public static final String WORLD_TICK = EventBus.register("WorldTick",
        new EventHandler<WorldTickEvent, EventPosterWorldTick>() {
        @Override
        public void callEvents (EventPosterWorldTick poster) {
            getEvents().forEach(event -> event.tick(poster.world, poster.delta));
        }
    });

    public static final String BLOCK_REPLACE = EventBus.register("BlockReplace",
        new EventHandler<BlockReplaceEvent, EventPosterBlockReplace>() {
        @Override
        public void callEvents (EventPosterBlockReplace poster) {
            getEvents().forEach(event -> event.handle(poster.world, poster.newBlock, poster.newBlock, poster.wx, poster.wy));
        }
    });

    public static final String WORLD_KEY_INPUT = EventBus.register("WorldKeyInput",
        new EventHandler<WorldKeyInputEvent, EventPosterWorldKeyInput>() {
        @Override
        public void callEvents (EventPosterWorldKeyInput poster) {
            getEvents().forEach(event -> event.process(poster.world, poster.key));
        }
    });

    public static final String WORLD_BUTTON_INPUT = EventBus.register("WorldButtonInput",
        new EventHandler<WorldButtonInputEvent, EventPosterWorldButtonInput>() {
            @Override
            public void callEvents (EventPosterWorldButtonInput poster) {
                getEvents().forEach(event -> event.process(poster.world, poster.screenX, poster.screenY, poster.pointer, poster.button));
            }
        });
}
