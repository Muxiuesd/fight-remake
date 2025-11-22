package ttk.muxiuesd.registrant;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 方块渲染器注册
 * */
public class BlockRendererRegistry extends Registries.DefaultRegistry<BlockRenderer<? extends Block>> {
    /**
     * 静态调用
     * */
    public static <T extends Block> BlockRenderer<T> register (T block, BlockRenderer<T> blockRenderer) {
        return getInstance().registerRenderer(block, blockRenderer);
    }

    /**
     * 获取渲染器
     * */
    public static <T extends Block> BlockRenderer<T> get (T block) {
        BlockRenderer<? extends Block> renderer = getInstance().get(block.getID());
        if (renderer == null) {
            Log.error(BlockRendererRegistry.class.getName(), "方块：" + block.getID() + " 的渲染器不存在！！！");
        }
        return (BlockRenderer<T>) renderer;
    }

    /// 单例模式
    private static BlockRendererRegistry INSTANCE;
    public static BlockRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new BlockRendererRegistry();
        }
        return INSTANCE;
    }

    /**
     * 基础注册方法
     * @param block 传入的方块必须持有id
     * */
    public <T extends Block> BlockRenderer<T> registerRenderer (T block, BlockRenderer<T> blockRenderer) {
        if (block != null && blockRenderer != null) {
            Identifier identifier = new Identifier(block.getID());
            if (contains(identifier)) {
                Log.print(this.getClass().getName(),
                    "已存在过方块：" + block.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            register(identifier, blockRenderer);
            return blockRenderer;
        }else {
            Log.error(this.getClass().getName(), "方块参数或者渲染器参数为null！！！");
            throw new IllegalArgumentException();
        }
    }
}
