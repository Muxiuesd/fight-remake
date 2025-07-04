package ttk.muxiuesd.world;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.WorldSystemsManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;

/**世界的基类
 * */
public abstract class World implements Updateable, Disposable {
    public final String TAG = this.getClass().getName();

    private final MainGameScreen screen;
    private WorldSystemsManager worldSystemsManager;

    public World(MainGameScreen screen) {
        this.screen = screen;
    }

    /**
     * 添加系统
     * */
    public <T extends WorldSystem> World addSystem(String name, T system) {
        this.getSystemManager().addSystem(name, system);
        return this;
    }

    /*@Override
    public void draw(Batch batch) {
        if (this.worldSystemsManager != null) {
            this.getSystemManager().draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        if (this.worldSystemsManager != null) {
            this.getSystemManager().renderShape(batch);
        }
    }*/

    @Override
    public void update(float delta) {
        if (this.worldSystemsManager != null) {
            this.getSystemManager().update(delta);
        }
    }

    @Override
    public void dispose() {
        if (this.worldSystemsManager != null) {
            this.getSystemManager().dispose();
        }
    }

    public WorldSystemsManager getSystemManager() {
        if (this.worldSystemsManager == null) {
            Log.error(TAG, "这个world的系统管理是null！！！");
            throw new RuntimeException();
        }
        return this.worldSystemsManager;
    }

    public void setWorldSystemsManager(WorldSystemsManager worldSystemsManager) {
        this.worldSystemsManager = worldSystemsManager;
    }

    public MainGameScreen getScreen() {
        return this.screen;
    }
}
