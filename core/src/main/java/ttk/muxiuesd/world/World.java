package ttk.muxiuesd.world;

import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.WorldInfoDataOutput;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.WorldSystemsManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.util.Log;

import java.util.Optional;

/**世界的基类
 * */
public abstract class World implements Updateable, Disposable {
    public final String TAG = this.getClass().getName();

    private final MainGameScreen screen;
    private WorldSystemsManager worldSystemsManager;

    public World(MainGameScreen screen) {
        this.screen = screen;

        //检查世界信息文件是否存在
        if(FileUtil.fileExists(Fight.PATH_SAVE, "worldInfo.json")) {
            //存在就读取
            String file = FileUtil.readFileAsString(Fight.PATH_SAVE, "worldInfo.json");
            Optional<WorldInfo> optional = WorldInfo.CODEC.parse(new JsonDataReader(file));
            if (optional.isPresent()) {
                //让这个实例存在
                WorldInfo.INSTANCE = optional.get();
            }
        }else {
            //新建一个
            WorldInfo.INSTANCE = new WorldInfo();
        }

    }

    /**
     * 添加系统
     * */
    public <T extends WorldSystem> World addSystem(String name, T system) {
        this.getSystemManager().addSystem(name, system);
        return this;
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
        JsonDataWriter dataWriter = new JsonDataWriter();
        dataWriter.objStart();
        WorldInfo.CODEC.encode(WorldInfo.INSTANCE, dataWriter);
        dataWriter.objEnd();
        new WorldInfoDataOutput().output(dataWriter);


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
