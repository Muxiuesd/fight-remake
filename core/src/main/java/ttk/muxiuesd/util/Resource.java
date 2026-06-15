package ttk.muxiuesd.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.id.Identifier;

/**
 * 游戏资源（贴图、音频等）持有类
 * */
public class Resource<T> {

    /**
     * 贴图
     * */
    public static Resource<TextureRegion> ofTextureRegion (String id, String path) {
        Identifier.checkAndThrow(id);
        return new Resource<>(id, Util.loadTextureRegion(id, path));
    }

    /**
     * 快捷加载并且设定资源的方法
     * @param path 默认的路径在游戏内部路径（assets/）目录下
     * */
    public static <T> Resource<T> of (String id, String path, Class<T> clazz) {
        Identifier.checkAndThrow(id);
        AssetsLoader.getInstance().loadAsync(id, path, clazz, null);
        return new Resource<>(id, AssetsLoader.getInstance().getById(id, clazz));
    }


    private Resource (String id, T resource) {
        this(Identifier.of(id), resource);
    }
    private Resource (Identifier identifier, T resource) {
        this.identifier = identifier;
        this.resource = resource;
    }

    private final Identifier identifier;    //这个资源的id
    private T resource;                     //资源

    /**
     * 获取资源
     * */
    public T get () {
        return this.resource;
    }

    /**
     * 设置对应的新资源
     * */
    public void setNew (T resource) {
        this.resource = resource;
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }
}
