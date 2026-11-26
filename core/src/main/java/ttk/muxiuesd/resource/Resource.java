package ttk.muxiuesd.resource;

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
     * 创建一个贴图资源
     * <p>
     * 延迟加载：构造时只注册 id→路径 映射（无 GL 调用，线程安全），
     * 首次 get() 时才触发实际贴图加载
     * */
    public static Resource<TextureRegion> ofTextureRegion (String id, String originalPath) {
        try {
            Identifier.checkAndThrow(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (originalPath != null) {
            Util.registerTextureIdPath(id, originalPath);
        }
        return new Resource<>(id, originalPath,
            null, () -> Util.loadTextureRegion(id, originalPath)
        );
    }

    /**
     * 创建一个水平镜像的贴图资源
     * <p>
     * 贴图是朝着某个方向画的实体（例如鸡的贴图朝右），实体朝反方向时就需要镜像绘制
     * <p>
     * 与原资源共用同一个 {@link Identifier}：这个副本在语义上就是同一张贴图的渲染变体，不是独立资源；
     * 且 {@link #ofTextureRegion} 对已注册的 id 不会重复注册路径映射，共用是安全的
     * <p>
     * 延迟加载：首次 {@link #get()} 时才从原资源取贴图并翻转，构造阶段不触发加载。
     * 原资源加载失败（为 null）时本资源同样为 null，由调用方判空处理
     * */
    public static Resource<TextureRegion> ofFlippedTextureRegion (Resource<TextureRegion> originalResource) {
        return new Resource<>(originalResource.getIdentifier(), null, null, () -> {
            TextureRegion region = originalResource.get();
            if (region == null) return null;
            //拷贝一份再翻转左右：flip 是原地修改且返回 void，不能直接改原贴图
            TextureRegion flippedRegion = new TextureRegion(region);
            flippedRegion.flip(true, false);
            return flippedRegion;
        });
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
