package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;

/**
 * 整个游戏的系统的抽象类
 * */
public abstract class GameSystem implements Updateable, Drawable, ShapeRenderable, Disposable {
    private SystemManager manager;

    @Override
    public void dispose () {

    }

    @Override
    public void draw (Batch batch) {

    }

    @Override
    public void renderShape (ShapeRenderer batch) {

    }

    @Override
    public void update (float delta) {

    }


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
