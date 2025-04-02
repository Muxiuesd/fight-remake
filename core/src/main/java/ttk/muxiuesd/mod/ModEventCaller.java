package ttk.muxiuesd.mod;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.abs.*;

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
        EventBus eventBus = EventBus.getInstance();
        ScriptEngine libEngine = ModLibManager.getInstance().getLibEngine();
        Invocable invocable = (Invocable) libEngine;
        eventBus.addEvent(EventBus.EventType.BulletShoot, new BulletShootEvent() {
            @Override
            public void call (Entity shooter, Bullet bullet) {
                try {
                    invocable.invokeFunction("callBulletShootEvent", shooter, bullet);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityAttacked, new EntityAttackedEvent() {
            @Override
            public void call (Entity attackObject, Entity victim) {
                try {
                    invocable.invokeFunction("callEntityAttackedEvent",  attackObject, victim);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityDeath, new EntityDeathEvent() {
            @Override
            public void call (LivingEntity deadEntity) {
                try {
                    invocable.invokeFunction("callEntityDeadEvent", deadEntity);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.KeyInput, new KeyInputEvent() {
            @Override
            public void call (int key) {
                try {
                    invocable.invokeFunction("callWorldKeyInputEvent", key);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.ButtonInput, new ButtonInputEvent() {
            @Override
            public void call (int screenX, int screenY, int pointer, int button) {
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
        });
    }
}
