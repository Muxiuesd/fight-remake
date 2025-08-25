package ttk.muxiuesd.system.manager;

import ttk.muxiuesd.system.abs.SystemManager;
import ttk.muxiuesd.util.Log;

/**
 * 游戏底层系统的管理
 * */
public final class GameSystemManager extends SystemManager {
    public static final String TAG = GameSystemManager.class.getName();
    private GameSystemManager() {
    }
    private static GameSystemManager INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new GameSystemManager();
            Log.print(TAG, "游戏底层系统管理初始化成功！");
        }
    }

    public static GameSystemManager getInstance() {
        return INSTANCE;
    }
}
