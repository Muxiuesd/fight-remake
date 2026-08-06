package ttk.muxiuesd.resource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.util.Util;

import java.util.function.Supplier;

/**
 * 游戏资源（贴图、音频等）持有类
 * <p>
 * 使用这个类包装的游戏资源与id相绑定。后续可以资源的id不变，但是资源本身可以热加载，类似于资源包形式
 * */
public class Resource<T> {

    /**
     * 贴图
     * <p>
     * 延迟加载：构造时只注册 id→路径 映射（无 GL 调用，线程安全），
     * 首次 get() 时才触发实际贴图加载
     * */
    public static Resource<TextureRegion> ofTextureRegion (String id, String originalPath) {
        Identifier.checkAndThrow(id);
        if (originalPath != null) {
            Util.registerTextureIdPath(id, originalPath);
        }
        return new Resource<>(id, originalPath, null, () -> Util.loadTextureRegion(id, originalPath));
    }

    /**
     * 快捷加载并且设定资源的方法
     * @param id 资源id
     * @param originalPath 原始资源路径。没有文件开头标记的话，默认路径在游戏内部路径（assets/）目录下
     * @param type 资源的类型
     * */
    public static <T> Resource<T> of (String id, String originalPath, Class<T> type) {
        Identifier.checkAndThrow(id);
        AssetsLoader.getInstance().load(id, originalPath, type, null);
        return new Resource<>(id, originalPath, AssetsLoader.getInstance().getById(id, type), null);
    }


    private Resource (String id, String originalPath, T resource, Supplier<T> loader) {
        this(Identifier.of(id), originalPath, resource, loader);
    }
    private Resource (Identifier identifier, String originalPath, T resource, Supplier<T> loader) {
        this.identifier = identifier;
        this.originalPath = originalPath;
        this.resource = resource;
        this.loader = loader;
    }

    private final Identifier identifier;    //这个资源的id
    private final String originalPath;      //原始的资源路径
    private T resource;                     //资源
    private Supplier<T> loader;             //延迟加载器

    /**
     * 获取资源（首次调用时若为延迟加载，会在此处触发）
     * */
    public T get () {
        if (this.resource != null) {
            return this.resource;
        }
        synchronized (this) {
            if (this.resource == null && this.loader != null) {
                this.resource = this.loader.get();
                this.loader = null;
            }
            return this.resource;
        }
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

    /**
     * 获取资源的原始文件路径
     * */
    public String getOriginalPath () {
        return this.originalPath;
    }
}
