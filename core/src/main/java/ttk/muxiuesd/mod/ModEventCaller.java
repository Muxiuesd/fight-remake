package ttk.muxiuesd.mod;

import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.abs.BulletShootEvent;
import ttk.muxiuesd.event.abs.EntityDeathEvent;
import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.event.abs.WorldTickEvent;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * mod事件的调用者
 * <p>
 * mod里的事件也得通过EventBus被调用
 * */
public class ModEventCaller {
    /**
     * 向EventBus添加mod里的事件调用，使得mod里注册的事件能被正确调用
     * */
    protected static void registryAllEventCaller () {
        ScriptEngine libEngine = ModLibManager.getInstance().getLibEngine();
        Invocable invocable = (Invocable) libEngine;
        EventBus.subscribe(EventTypes.BULLET_SHOOT, new BulletShootEvent() {
            @Override
            public void handle (World world, Entity shooter, Bullet bullet) {
                try {
                    invocable.invokeFunction("callBulletShootEvent", world, shooter, bullet);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        EventBus.subscribe(EventTypes.ENTITY_HURT, new EntityHurtEvent() {
            @Override
            public void handle (World world, Entity attackObject, Entity victim) {
                try {
                    invocable.invokeFunction("callEntityAttackedEvent", world, attackObject, victim);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        EventBus.subscribe(EventTypes.ENTITY_DEATH, new EntityDeathEvent() {
            @Override
            public void handle (World world, LivingEntity entity) {
                try {
                    invocable.invokeFunction("callEntityDeadEvent", world, entity);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        EventBus.subscribe(EventTypes.WORLD_TICK, new WorldTickEvent() {
            @Override
            public void tick (World world, float delta) {
                try {
                    invocable.invokeFunction("callWorldTickEvent", world, delta);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        /*EventBus eventBus = EventBus.getInstance();
        ScriptEngine libEngine = ModLibManager.getInstance().getLibEngine();
        Invocable invocable = (Invocable) libEngine;
        eventBus.addEvent(EventBus.EventType.BulletShoot, new BulletShootEvent() {
            @Override
            public void callback (Entity shooter, Bullet bullet) {
                try {
                    invocable.invokeFunction("callBulletShootEvent", shooter, bullet);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityAttacked, new EntityAttackedEvent() {
            @Override
            public void callback (Entity attackObject, Entity victim) {
                try {
                    invocable.invokeFunction("callEntityAttackedEvent",  attackObject, victim);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityDeath, new EntityDeathEvent() {
            @Override
            public void callback (LivingEntity deadEntity) {
                try {
                    invocable.invokeFunction("callEntityDeadEvent", deadEntity);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.KeyInput, new KeyInputEvent() {
            @Override
            public void callback (int key) {
                try {
                    invocable.invokeFunction("callWorldKeyInputEvent", key);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.ButtonInput, new ButtonInputEvent() {
            @Override
            public void callback (int screenX, int screenY, int pointer, int button) {
                try {
                    invocable.invokeFunction("callWorldButtonInputEvent", screenX, screenY, pointer, button);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.TickUpdate, new WorldTickUpdateEvent() {
            @Override
            public void tick (World world, float delta) {
                try {
                    invocable.invokeFunction("callWorldTickEvent", world, delta);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });*/
    }
}
