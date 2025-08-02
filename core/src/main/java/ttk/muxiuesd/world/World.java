package ttk.muxiuesd.world;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.WorldInfoDataOutput;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.WorldSystemsManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;

/**世界的基类
 * */
public abstract class World implements Updateable, Disposable {
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

    /**
     * 获取世界的系统
     * */
    public <T extends WorldSystem> T getSystem(Class<T> systemClass) {
        return this.getSystemManager().getSystem(systemClass);
    }


    @Override
    public void update(float delta) {
        if (this.worldSystemsManager != null) {
            this.getSystemManager().update(delta);
        }
    }

    @Override
    public void dispose() {
        //编写信息文件
        this.writeWorldInfo();

        if (this.worldSystemsManager != null) {
            this.getSystemManager().dispose();
        }
    }

    private void writeWorldInfo () {
        try {
            JsonDataWriter dataWriter = new JsonDataWriter();
            dataWriter.objStart();
            WorldInfo.CODEC.encode(WorldInfo.INSTANCE, dataWriter);
            dataWriter.objEnd();
            new WorldInfoDataOutput().output(dataWriter);
        }catch (Exception e) {
            Log.error(TAG(), "世界信息写入失败！！！原因：", e);
        }
        Log.print(TAG(), "世界信息写入完成。");
    }

    public WorldSystemsManager getSystemManager() {
        if (this.worldSystemsManager == null) {
            Log.error(TAG(), "这个world的系统管理是null！！！");
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

    public String TAG () {
        return this.getClass().getName();
    }
}
