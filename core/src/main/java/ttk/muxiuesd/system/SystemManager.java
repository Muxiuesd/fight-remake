package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * 游戏系统的管理者
 * */
public class SystemManager implements Updateable, Drawable, ShapeRenderable, Disposable {
    public final String TAG = this.getClass().getName();

    private final HashMap<String, WorldSystem> systems;

    public SystemManager() {
        this.systems = new HashMap<>();
    }

    public SystemManager addSystem(String name, WorldSystem system) {
        if (!this.systems.containsKey(name)) {
            //system.setManager(this);
            this.systems.put(name, system);
            return this;
        }
        //TODO
        return this;
    }

    public WorldSystem getSystem(String name) {
        if (systems.containsKey(name)) {
            return systems.get(name);
        }
        Log.print(TAG, "无法获取名为 "+ name +" 的系统！！！");
        return null;
    }

    @Override
    public void draw(Batch batch) {
        this.systems.values().forEach(system -> system.draw(batch));
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        this.systems.values().forEach(system -> system.renderShape(batch));
    }

    @Override
    public void update(float delta) {
        this.systems.values().forEach(system -> system.update(delta));
    }

    @Override
    public void dispose() {
        this.systems.values().forEach(Disposable::dispose);
    }
}
