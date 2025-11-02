package ttk.muxiuesd.system;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterWorldTick;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.registry.WorldInformationType;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Info;
import ttk.muxiuesd.world.World;

/**
 * 时间系统
 * <p>
 * tick更新为不精确更新，无法保证每秒一定更新多少次，但会传入每两次tick更新的间隔以供使用
 * */
public class TimeSystem extends WorldSystem implements Tickable {
    public static final Info<Float> GAME_TIME = Info.create("game_time", 0f);

    public static final float REAL_SECOND_TO_GAME_MINUTE = 10f; // 游戏时间参数（1秒现实=10分钟游戏时间）
    public static final int TicksPerSecond = 20;
    public static final float TickMaxSpan = 1f / TicksPerSecond;

    private float tickSpan = 0f;
    private float gameTime = 0f;    //游戏内的时间

    private final Array<Tickable> tickUpdates;
    private final Array<Tickable> _delayAdd;
    private final Array<Tickable> _delayRemove;

    public TimeSystem (World world) {
        super(world);
        this.tickUpdates = new Array<>();
        this._delayAdd = new Array<>();
        this._delayRemove = new Array<>();
        WorldInformationType.FLOAT.putIfNull(GAME_TIME);

        this.gameTime = WorldInformationType.FLOAT.get(GAME_TIME);
    }

    @Override
    public void update (float delta) {
        if (!this._delayAdd.isEmpty()) {
            this.tickUpdates.addAll(this._delayAdd);
            this._delayAdd.clear();
        }
        if (!this._delayRemove.isEmpty()) {
            this.tickUpdates.removeAll(this._delayRemove, true);
            this._delayRemove.clear();
        }

        // 累计游戏时间（delta为现实时间秒）
        this.gameTime += delta * REAL_SECOND_TO_GAME_MINUTE / 60f;
        // 24小时循环
        if(this.gameTime >= 24f) {
            this.gameTime -= 24f;
        }

        if (this.tickSpan >= TickMaxSpan) {
            this.tick(getWorld(), this.tickSpan);
            this.tickSpan = 0f;
        }else {
            this.tickSpan += delta;
        }
    }

    @Override
    public void tick (World world, float delta) {
        //更新所有的tick
        this.tickUpdates.forEach(t -> t.tick(world, delta));

        EventBus.post(EventTypes.WORLD_TICK, new EventPosterWorldTick(getWorld(), delta));
    }

    @Override
    public void dispose () {
        WorldInformationType.FLOAT.put(GAME_TIME.getKey(), this.getGameTime());
    }

    /**
     * 添加tick更新
     * */
    public void add (Tickable tickable) {
        //防止重复添加
        if (this._delayAdd.contains(tickable, true)
            || this.tickUpdates.contains(tickable, true)) return;

        this._delayAdd.add(tickable);
    }

    /**
     * 移除tick更新
     * */
    public void remove (Tickable tickable) {
        this._delayRemove.add(tickable);
    }

    /**
     * 获取当前游戏内的时间
     * */
    public float getGameTime () {
        return this.gameTime;
    }

    public boolean isDay () {
        return this.gameTime > 0 && this.gameTime <= 9.6;
    }

    public boolean isDusk () {
        return this.gameTime > 9.6 && this.gameTime <= 14.4;
    }

    public boolean isNight () {
        return this.gameTime > 14.4 && this.gameTime <= 24;
    }
}
