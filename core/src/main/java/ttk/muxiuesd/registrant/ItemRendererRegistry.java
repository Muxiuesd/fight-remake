package ttk.muxiuesd.registrant;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品的渲染器注册
 * */
public class ItemRendererRegistry extends Registries.DefaultRegistry<ItemRenderer<? extends Item>>{
    /**
     * 静态调用
     * */
    public static <T extends Item> ItemRenderer<T> register (T item, ItemRenderer<T> itemRenderer) {
        return getInstance().registerRenderer(item, itemRenderer);
    }

    /**
     * 获取渲染器
     * */
    public static <T extends Item> ItemRenderer<T> get (T item) {
        ItemRenderer<? extends Item> renderer = getInstance().get(item.getID());
        if (renderer == null) {
            Log.error(ItemRendererRegistry.class.getName(), "物品：" + item.getID() + " 的渲染器不存在！！！");
        }
        return (ItemRenderer<T>) renderer;
    }

    /// 单例模式
    private static ItemRendererRegistry INSTANCE;
    public static ItemRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new ItemRendererRegistry();
        }
        return INSTANCE;
    }

    /**
     * 基础注册方法
     * @param item 传入的物品必须持有id
     * */
    public <T extends Item> ItemRenderer<T> registerRenderer (T item, ItemRenderer<T> itemRenderer) {
        if (item != null && itemRenderer != null) {
            Identifier identifier = new Identifier(item.getID());
            if (contains(identifier)) {
                Log.print(this.getClass().getName(),
                    "已存在过物品：" + item.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            register(identifier, itemRenderer);
            return itemRenderer;
        }else {
            Log.error(this.getClass().getName(), "物品参数或者渲染器参数为null！！！");
            throw new IllegalArgumentException();
        }
    }
}
