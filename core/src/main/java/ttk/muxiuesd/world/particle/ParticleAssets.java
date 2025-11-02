package ttk.muxiuesd.world.particle;

import com.badlogic.gdx.graphics.Texture;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;

/**
 * 粒子贴图资产注册
 * <p>
 * TODO json文件定义粒子
 * */
public class ParticleAssets {
    public static void init () {}

    public static final String SPELL = loadTexture("spell", "texture/particle/spell.png");
    public static final String BUBBLE = loadTexture("bubble", "texture/particle/bubble.png");
    public static final String FIRE = loadTexture("fire", "texture/entity/bullet/flame.png");


    /**
     * 加载粒子贴图
     * */
    public static String loadTexture (String name, String path) {
        String id = Fight.ID(name);
        AssetsLoader.getInstance().loadAsync(id, path, Texture.class, () -> {});
        return id;
    }
}
