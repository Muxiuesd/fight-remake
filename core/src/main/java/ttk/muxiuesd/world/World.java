package ttk.muxiuesd.world;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.WorldSystemsManager;
import ttk.muxiuesd.util.Log;

/**世界的基类
 * */
public abstract class World implements Updateable, Drawable, ShapeRenderable, Disposable {
    public final String TAG = this.getClass().getName();

    private MainGameScreen screen;
    private WorldSystemsManager worldSystemsManager;

    public World(MainGameScreen screen) {
        this.screen = screen;
    }

    @Override
    public void draw(Batch batch) {
        if (worldSystemsManager != null) {
            getSystemManager().draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        if (worldSystemsManager != null) {
            getSystemManager().renderShape(batch);
        }
    }

    @Override
    public void update(float delta) {
        if (worldSystemsManager != null) {
            getSystemManager().update(delta);
        }
    }

    @Override
    public void dispose() {
        if (worldSystemsManager != null) {
            getSystemManager().dispose();
        }
    }

    public WorldSystemsManager getSystemManager() {
        if (this.worldSystemsManager == null) {
            Log.error(TAG, "这个world的系统管理是null！！！");
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
