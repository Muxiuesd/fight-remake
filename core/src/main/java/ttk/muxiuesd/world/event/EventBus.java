package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.mod.Mod;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.HashMap;

/**
 * 事件总线
 * */
public class EventBus {
    public final String TAG = this.getClass().getName();

    //事件组id
    public static final int BulletShoot = 0;
    public static final int EntityAttacked = 1;

    //单例模式，游戏里的唯一事件总线
    private static EventBus instance;
    private HashMap<Integer, EventGroup> eventGroups;

    private EventBus() {
        eventGroups = new HashMap<>();
        eventGroups.put(BulletShoot, new EventGroup<BulletShootEvent>());
        eventGroups.put(EntityAttacked, new EventGroup<EntityAttackedEvent>());
    }

    public void initialize() {
        addEvent(BulletShoot, new BulletShootEvent() {
            @Override
            public void call (Entity shooter, Bullet bullet) {
                Log.print(TAG, "射击者：" + shooter + " 射出子弹：" + bullet);
                //调用mod里的事件
                HashMap<String, Mod> mods = ModLoader.getInstance().getMods();
                for (Mod mod : mods.values()) {
                    Invocable invocable = (Invocable) mod.getEngine();
                    try {
                        invocable.invokeFunction("callBulletShootEvent", shooter, bullet);
                    } catch (ScriptException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        addEvent(EntityAttacked, new EntityAttackedEvent() {
            @Override
            public void call (Entity attackObject, Entity victim) {

            }
        });
    }

    public boolean addEvent (int eventGroupId, Event event) {
        if (eventGroups.containsKey(eventGroupId)) {
            return eventGroups.get(eventGroupId).addEvent(event);
        }
        return false;
    }

    public EventGroup getEventGroup (int eventGroupId) {
        if (this.eventGroups.containsKey(eventGroupId)) {
            return this.eventGroups.get(eventGroupId);
        }
        Log.error(TAG, "eventGroupId：" + eventGroupId+ " 不存在！！！");
        return null;
    }

    public static EventBus getInstance () {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
}
