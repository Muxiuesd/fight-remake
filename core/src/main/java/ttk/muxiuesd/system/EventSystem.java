package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventSlimeDead;

public class EventSystem extends WorldSystem {
    public EventSystem(World world) {
        super(world);
        this.initAllEvents();
    }

    private void initAllEvents () {
        EventBus bus = EventBus.getInstance();
        //添加游戏内事件
        bus.addEvent(EventBus.EventType.EntityDeath, new EventSlimeDead(getWorld()));
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }
}
