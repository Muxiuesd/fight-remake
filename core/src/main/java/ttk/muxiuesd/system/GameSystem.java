package ttk.muxiuesd.system;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.World;

/**
 * 所有的系统的基类
 * */
public abstract class GameSystem implements Updateable, Drawable, ShapeRenderable, Disposable {
    private World world;

    public GameSystem(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
