package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.world.World;

/**
 * 运行在世界里的系统
 * */
public abstract class WorldSystem extends GameSystem {
    private World world;

    public WorldSystem(World world) {
        this.world = world;
        setManager(world.getSystemManager());
    }

    /**
     * 延迟初始化
     * */
    public void initialize () {

    }

    public World getWorld() {
        return world;
    }

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
}
