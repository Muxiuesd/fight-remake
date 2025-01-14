package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.World;

/**
 * 运行在世界里的系统
 * */
public abstract class WorldSystem extends FightSystem {
    private World world;

    public WorldSystem(World world) {
        this.world = world;
        setManager(world.getSystemManager());
    }

    public World getWorld() {
        return world;
    }
}
