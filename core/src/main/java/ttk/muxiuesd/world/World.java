package ttk.muxiuesd.world;

import com.badlogic.gdx.utils.Disposable;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.WorldInfoDataOutput;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.manager.WorldSystemsManager;
import ttk.muxiuesd.util.FileUtil;

import java.util.Optional;

/**
 * 世界的基类
 * */
public abstract class World implements Updateable, Disposable {
    private final MainGameScreen screen;
    private WorldSystemsManager worldSystemsManager;


    public World(MainGameScreen screen) {
        this.screen = screen;
        this.readWorldInfo();
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
        if (this.worldSystemsManager != null) {
            this.getSystemManager().dispose();
        }

        //写入世界名称
        WorldInfoTypes.STRING.putIfNull(Fight.WORLD_NAME);
        //编写信息文件
        this.writeWorldInfo();
    }

    /**
     * 设置世界的名称
     * */
    public static void setWorldName(String worldName) {
        if (worldName == null || !worldName.isBlank()) {
            Log.error(Fight.class.getName(), "设定的世界名称：" + worldName + " 不合法！！！");
        }else {
            Fight.WORLD_NAME.setValue(worldName);
        }
    }
    /**
     * 获取世界名称
     * */
    public String getWorldName() {
        return Fight.WORLD_NAME.getValue();
    }

    /**
     * 读取世界信息
     * */
    public void readWorldInfo() {
        //检查世界信息文件是否存在
        if(FileUtil.fileExists(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME)) {
            //存在就读取
            String file = FileUtil.readFileAsString(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME);
            Optional<WorldInfo> optional = WorldInfo.CODEC.parse(new JsonDataReader(file));
            //让这个实例存在
            optional.ifPresent(worldInfo -> WorldInfo.INSTANCE = worldInfo);
        }else {
            //如果不存在，就新建一个
            WorldInfo.INSTANCE = new WorldInfo();
        }
    }

    /**
     * 写入世界信息
     * */
    private void writeWorldInfo () {
        try {
            JsonDataWriter dataWriter = new JsonDataWriter();
            dataWriter.objStart();
            WorldInfo.CODEC.encode(WorldInfo.INSTANCE, dataWriter);
            dataWriter.objEnd();
            //输出
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

    /**
     * 用于debug的信息
    * */
    public String TAG () {
        return this.getClass().getName();
    }
}
