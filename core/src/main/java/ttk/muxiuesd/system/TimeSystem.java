package ttk.muxiuesd.system;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventGroup;
import ttk.muxiuesd.world.event.abs.WorldTickUpdateEvent;

/**
 * 时间系统
 * <p>
 * tick更新为不精确更新，无法保证每秒一定更新多少次，但会传入每两次tick更新的间隔以供使用
 * */
public class TimeSystem extends WorldSystem implements Tickable {
    // 游戏时间参数（1秒现实=5分钟游戏时间）
    public static final float REAL_SECOND_TO_GAME_MINUTE = 5f;
    public static final int TicksPerSecond = 20;
    public static final float TickMaxSpan = 1f / TicksPerSecond;

    private float tickSpan = 0f;
    private float gameTime = 0f;    //游戏内的时间

    private final Array<Tickable> tickUpdates;
    private final Array<Tickable> delayAdd;
    private final Array<Tickable> delayRemove;

    public TimeSystem (World world) {
        super(world);
        this.tickUpdates = new Array<>();
        this.delayAdd = new Array<>();
        this.delayRemove = new Array<>();
    }

    @Override
    public void update (float delta) {
        if (this.delayAdd.size > 0) {
            this.tickUpdates.addAll(this.delayAdd);
            this.delayAdd.clear();
        }
        if (this.delayRemove.size > 0) {
            this.tickUpdates.removeAll(this.delayRemove, true);
            this.delayRemove.clear();
        }

        // 累计游戏时间（delta为现实时间秒）
        this.gameTime += delta * REAL_SECOND_TO_GAME_MINUTE / 60f;
        // 24小时循环
        if(this.gameTime >= 24f) this.gameTime -= 24f;

        //System.out.println("游戏内时间：" + this.gameTime);

        if (this.tickSpan >= TickMaxSpan) {
            this.tick(this.tickSpan);
            this.tickSpan = 0f;
        }else {
            this.tickSpan += delta;
        }
    }

    @Override
    public void tick (float delta) {
        //更新所有的tick
        this.tickUpdates.forEach(t -> t.tick(delta));

        this.callWorldTickEvent(delta);
    }

    /**
     * 添加tick更新
     * */
    public void add(Tickable tickable) {
        this.delayAdd.add(tickable);
    }

    /**
     * 移除tick更新
     * */
    public void remove(Tickable tickable) {
        this.delayRemove.add(tickable);
    }

    /**
     * 调用相关事件
     * */
    public void callWorldTickEvent  (float delta) {
        EventGroup<WorldTickUpdateEvent> eventGroup = EventBus.getInstance().getEventGroup(EventBus.EventType.TickUpdate);
        eventGroup.getEvents().forEach(e -> {
            e.tick(getWorld(), delta);
        });
    }


    /**
     * 获取当前游戏内的时间
     * */
    public float getGameTime () {
        return this.gameTime;
    }
}
