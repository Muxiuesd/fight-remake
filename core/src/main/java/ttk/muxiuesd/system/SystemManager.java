package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 游戏系统的管理者
 * */
public class SystemManager implements Updateable, Drawable, ShapeRenderable, Disposable {
    public final String TAG = this.getClass().getName();

    private final LinkedHashMap<String, WorldSystem> systems; //使用LinkedHashMap确保初始化的顺序为添加系统时的顺序

    public SystemManager() {
        this.systems = new LinkedHashMap<>();
    }

    public SystemManager addSystem(String name, WorldSystem system) {
        if (!this.systems.containsKey(name)) {
            this.systems.put(name, system);
            return this;
        }
        throw new RuntimeException("名称为：" + name + " 的系统已存在！！！");
    }

    public WorldSystem getSystem(String name) {
        if (systems.containsKey(name)) {
            return systems.get(name);
        }
        Log.error(TAG, "无法获取名为 "+ name +" 的系统！！！");
        throw new RuntimeException("名称为：" + name + " 的系统不存在！！！");
    }

    /**
     * 延迟初始化
     * */
    public void initAllSystems() {
        for (WorldSystem system : systems.values()) {
            system.initialize();
        }
        Log.print(TAG, "所有系统初始化完毕");
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
