package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.utils.PoolManager;

/**
 * 全局类：对象池管理
 * */
public class PoolsManager {
    private static final PoolManager poolManager = new PoolManager();

    public static PoolManager getInstance() {
        return poolManager;
    }
}
