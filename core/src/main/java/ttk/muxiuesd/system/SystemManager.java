package ttk.muxiuesd.system;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 游戏系统的管理者
 * */
public class SystemManager implements Updateable, Disposable {
    public final String TAG = this.getClass().getName();

    private final LinkedHashMap<Class<? extends WorldSystem>, String> systemsClazzToName;
    private final LinkedHashMap<String, WorldSystem> systems; //使用LinkedHashMap确保初始化的顺序为添加系统时的顺序


    public SystemManager() {
        this.systemsClazzToName = new LinkedHashMap<>();
        this.systems = new LinkedHashMap<>();
    }

    public SystemManager addSystem(String name, WorldSystem system) {
        if (!this.systems.containsKey(name)) {
            this.systemsClazzToName.put(system.getClass(), name);
            this.systems.put(name, system);

            return this;
        }
        //存在同名系统，就执行覆盖
        WorldSystem oldSystem = this.systems.get(name);
        this.systemsClazzToName.remove(oldSystem.getClass());
        this.systemsClazzToName.put(system.getClass(), name);
        this.systems.put(name, system);
        Log.print(TAG, "旧系统：" + oldSystem + " 已被新系统：" + system + " 覆盖！");

        return this;
    }

    public WorldSystem getSystem (String name) {
        if (this.systems.containsKey(name)) {
            return this.systems.get(name);
        }
        Log.error(TAG, "无法获取名为 "+ name +" 的系统！！！");
        throw new RuntimeException("名称为：" + name + " 的系统不存在！！！");
    }

    /**
     * 通过类名来获取系统
     * */
    public <T extends WorldSystem> T getSystem(Class<T> clazz) {
        if (! this.systemsClazzToName.containsKey(clazz)) {
            Log.error(TAG, "无法获取类为 "+ clazz +" 的系统！！！");
        }
        String name = this.systemsClazzToName.get(clazz);
        return (T) this.systems.get(name);
    }

    /**
     * 延迟初始化
     * */
    public void initAllSystems() {
        for (WorldSystem system : systems.values()) {
            system.initialize();
            if (system instanceof IRenderTask task) {
                RenderProcessorManager.addRenderTask(task);
            }
        }
        Log.print(TAG, "所有系统初始化完毕");
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
