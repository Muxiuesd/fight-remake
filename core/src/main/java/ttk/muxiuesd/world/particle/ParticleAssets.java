package ttk.muxiuesd.world.particle;

import com.badlogic.gdx.graphics.Texture;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;

/**
 * 粒子资产
 * <p>
 * TODO json文件定义粒子
 * */
public class ParticleAssets {
    public static void loadAll () {
        loadTexture("spell", "texture/particle/spell.png");
    }

    private static void loadTexture (String name, String path) {
        AssetsLoader.getInstance().loadAsync(Fight.getId(name), path, Texture.class, null);
    }
}
