package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;

/**
 * 整个游戏的系统的抽象类
 * */
public abstract class GameSystem implements Updateable, Drawable, ShapeRenderable, Disposable {
    private SystemManager manager;

    /**
     * 延迟初始化
     * */
    public void initialize () {
    }

    public SystemManager getManager() {
        return this.manager;
    }

    public void setManager(SystemManager manager) {
        this.manager = manager;
    }

    public String TAG () {
        return this.getClass().getName();
    }
}
