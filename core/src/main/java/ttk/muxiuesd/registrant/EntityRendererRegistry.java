package ttk.muxiuesd.registrant;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体的渲染器注册
 * */
public class EntityRendererRegistry extends Registries.DefaultRegistry<EntityRenderer<? extends Entity<?>>> {
    /**
     * 静态调用
     * */
    public static <T extends Entity<?>> EntityRenderer<T> register (T entity, EntityRenderer<T> blockRenderer) {
        return getInstance().registerRenderer(entity, blockRenderer);
    }

    /**
     * 获取渲染器
     * */
    public static <T extends Entity<?>> EntityRenderer<T> get (T entity) {
        EntityRenderer<? extends Entity<?>> renderer = getInstance().get(entity.getID());
        if (renderer == null) {
            Log.error(EntityRendererRegistry.class.getName(), "实体：" + entity.getID() + " 的渲染器不存在！！！");
        }
        return (EntityRenderer<T>) renderer;
    }


    /// 单例模式
    private static EntityRendererRegistry INSTANCE;
    public static EntityRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new EntityRendererRegistry();
        }
        return INSTANCE;
    }

    public <T extends Entity<?>> EntityRenderer<T> registerRenderer (T entity, EntityRenderer<T> entityRenderer) {
        if (entity != null && entityRenderer != null) {
            Identifier identifier = new Identifier(entity.getID());
            if (contains(identifier)) {
                Log.print(this.getClass().getName(),
                    "已存在过实体：" + entity.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            register(identifier, entityRenderer);
            return entityRenderer;
        }else {
            Log.error(this.getClass().getName(), "实体参数或者渲染器参数为null！！！");
            throw new IllegalArgumentException();
        }
    }
}
