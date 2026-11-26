package ttk.muxiuesd.world;

import com.badlogic.gdx.utils.Disposable;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.WorldInfoDataOutput;
import ttk.muxiuesd.interfaces.FixedTickable;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.manager.WorldSystemsManager;

/**
 * 世界的基类
 * */
public abstract class World implements Updateable, FixedTickable, Disposable {
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

    /**
     * 逻辑帧（tick）更新
     * <p>
     * 由屏幕以固定频率调用，delta 恒为固定步长（不会因为掉帧变大）。
     * 与 update 的分工：
     * update 用真实帧间隔，负责"接触外部"——输入采样、位移执行与碰撞修正、各种计时器、区块 IO、相机/粒子/UI；
     * tick 用固定步长，负责"内部演化"——实体 AI 决策、速度积分、方块实体与作物、WORLD_TICK 事件。
     * <p>
     * 位移与碰撞留在 update 里、速度由 tick 算，所以位置永远是"拿最近一次 tick 算出的速度"逐帧积分出来的：
     * 帧率再高也不会多算一次摩擦，帧率再低也不会少走一段路
     * */
    @Override
    public void tick (float tickSpan) {
        if (this.worldSystemsManager == null) return;
        this.getSystemManager().getSystem(TimeSystem.class).tick(this, tickSpan);
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
        if (worldName == null || worldName.isBlank()) {
            Log.error(Fight.class.getName(), "设定的世界名称：" + worldName + " 不合法！！！");
        }else {
            Fight.WORLD_NAME.setValue(worldName);
        }
    }
    /**
     * 获取世界名称
     * */
    public String getWorldName () {
        return Fight.WORLD_NAME.getValue();
    }

    /**
     * 读取世界信息
     * */
    public void readWorldInfo () {
        //切档清理：清空全局静态的世界信息map，避免上一个存档的字段残留到本次存档
        //（WorldInfoTypes 的 map 是全局共享单例，decode 只会 put、从不 clear，
        //若新档缺失某些键，旧档残留值会被新世界读到 → 串档/用错出生点、种子等）
        Registries.WORLD_INFO_HASH_MAP.getMap().forEach((key, value) -> {
            value.clear();
        });

        //检查世界信息文件是否存在
        if(UnifiedFileUtil.fileExists(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME)) {
            //存在就读取
            String file = UnifiedFileUtil.readFileAsString(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME);
            RawObject raw = RawObjectJsonConverter.fromJson(file);
            DataResult<WorldInfo> result = WorldInfo.CODEC.decode(raw);
            //让这个实例存在（error 但 result 有值时也使用解码结果；完全失败时兜底新建）
            if (result.result().isPresent()) {
                WorldInfo.setInstance(result.result().get());
            } else {
                WorldInfo.setInstance(new WorldInfo());
            }
        }else {
            //如果不存在，就新建一个
            WorldInfo.setInstance(new WorldInfo());
        }
    }

    /**
     * 写入世界信息
     * */
    private void writeWorldInfo () {
        try {
            RawObject raw = WorldInfo.CODEC.encode(WorldInfo.getInstance());
            String json = RawObjectJsonConverter.toJson(raw);
            //输出
            new WorldInfoDataOutput().output(json);
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


