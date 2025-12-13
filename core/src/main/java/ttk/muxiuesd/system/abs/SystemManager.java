package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.util.Log;

import java.util.LinkedHashMap;

/**
 * 游戏系统的管理者
 * */
public abstract class SystemManager implements Updateable, Disposable {
    public final String TAG = this.getClass().getName();

    private final LinkedHashMap<Class<? extends GameSystem>, String> systemsClazzToName;
    private final LinkedHashMap<String, GameSystem> systems; //使用LinkedHashMap确保初始化的顺序为添加系统时的顺序


    public SystemManager() {
        this.systemsClazzToName = new LinkedHashMap<>();
        this.systems = new LinkedHashMap<>();
    }

    /**
     * 添加系统，如果系统实现了渲染处理任务，会自动添加（需要对应的渲染处理器已经注册完毕）
     * */
    public SystemManager addSystem(String name, GameSystem system) {
        if (!this.systems.containsKey(name)) {
            this.systemsClazzToName.put(system.getClass(), name);
            this.systems.put(name, system);
        }else {
            //存在同名系统，就执行覆盖
            GameSystem oldSystem = this.systems.get(name);
            this.systemsClazzToName.remove(oldSystem.getClass());
            //如果旧的系统实现了渲染接口，就移除它的渲染任务
            if (system instanceof IRenderTask task) {
                this.removeSystemRenderTask(task);
            }
            this.systemsClazzToName.put(system.getClass(), name);
            this.systems.put(name, system);
            Log.print(TAG, "旧系统：" + oldSystem + " 已被新系统：" + system + " 覆盖！");
        }
        //如果系统实现了渲染接口
        if (system instanceof IRenderTask task) {
            this.addSystemRenderTask(task);
        }

        return this;
    }

    public GameSystem getSystem (String name) {
        if (this.systems.containsKey(name)) {
            return this.systems.get(name);
        }
        Log.error(TAG, "无法获取名为 "+ name +" 的系统！！！");
        throw new RuntimeException("名称为：" + name + " 的系统不存在！！！");
    }

    /**
     * 通过类名来获取系统
     * */
    public <T extends GameSystem> T getSystem(Class<T> clazz) {
        if (! this.systemsClazzToName.containsKey(clazz)) {
            Log.error(TAG, "无法获取类为 "+ clazz +" 的系统！！！");
        }
        String name = this.systemsClazzToName.get(clazz);
        return (T) this.systems.get(name);
    }

    /**
     * 延迟初始化，当所有系统添加完毕后的初始化
     * */
    public void initAllSystems() {
        for (GameSystem system : this.systems.values()) {
            system.initialize();
        }
        Log.print(TAG, "所有系统初始化完毕");
    }

    /**
     * 将实现了渲染任务接口的系统加入渲染任务管理里面
     * */
    public void addSystemRenderTask (IRenderTask systemRenderTask) {
        RenderProcessorManager.addRenderTask(systemRenderTask);
    }
    /**
     * 将实现了渲染任务接口的系统加入渲染任务管理里面
     * */
    public void removeSystemRenderTask (IRenderTask systemRenderTask) {
        RenderProcessorManager.removeRenderTask(systemRenderTask);
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
