package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;

import java.util.HashMap;

/**
 * 方块渲染器注册
 * */
public class BlockRendererRegistry {
    /**
     * 静态调用
     * */
    public static <T extends Block> BlockRenderer<T> register (T block, BlockRenderer<T> blockRenderer) {
        return getInstance().registerRenderer(block, blockRenderer);
    }

    public static <T extends Block> BlockRenderer<T> getRenderer (T block) {
        BlockRenderer<? extends Block> renderer = getInstance().getIdToRenderersMap().get(block.getID());
        if (renderer == null) {
            Log.error(BlockRendererRegistry.class.getName(), "方块：" + block.getID() + " 的渲染器不存在！！！");
        }
        return (BlockRenderer<T>) renderer;
    }

    private static BlockRendererRegistry INSTANCE;
    public static BlockRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new BlockRendererRegistry();
        }
        return INSTANCE;
    }

    private final HashMap<Block, BlockRenderer<? extends Block>> blockRenderersMap = new HashMap<>();
    private final HashMap<String, BlockRenderer<? extends Block>> idToRenderersMap = new HashMap<>();

    /**
     * 基础注册方法
     * @param block 传入的方块必须持有id
     * */
    public <T extends Block> BlockRenderer<T> registerRenderer (T block, BlockRenderer<T> blockRenderer) {
        if (block != null && blockRenderer != null) {
            if (this.getBlockRenderersMap().containsKey(block) || this.getIdToRenderersMap().containsKey(block.getID())) {
                Log.print(this.getClass().getName(),
                    "已存在过方块：" + block.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            this.getBlockRenderersMap().put(block, blockRenderer);
            this.getIdToRenderersMap().put(block.getID(), blockRenderer);
            return blockRenderer;
        }else {
            Log.error(this.getClass().getName(), "方块参数或者渲染器参数为null！！！");
        }
        return null;
    }


    public HashMap<Block, BlockRenderer<? extends Block>> getBlockRenderersMap () {
        return this.blockRenderersMap;
    }

    public HashMap<String, BlockRenderer<? extends Block>> getIdToRenderersMap () {
        return this.idToRenderersMap;
    }
}
