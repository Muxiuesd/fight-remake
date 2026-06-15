package ttk.muxiuesd.resource;

import com.badlogic.gdx.assets.AssetManager;

/**
 * 魔改的资源管理器，用于适配统一文件工具的标准
 * */
public class FightAssetManager extends AssetManager {
    public FightAssetManager() {
        super(new FightFileHandleResolver());
    }
}
