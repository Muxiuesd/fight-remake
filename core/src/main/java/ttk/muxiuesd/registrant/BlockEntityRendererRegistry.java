package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;

import java.util.HashMap;

/**
 * 方块实体渲染器注册
 * */
public class BlockEntityRendererRegistry {
    /**
     * 静态调用
     * */
    public static <T extends BlockWithEntity, E extends BlockEntity> BlockEntityRenderer<E>
    register (T blockWithEntity, BlockEntityRenderer<E> blockRenderer) {
        return getInstance().registerRenderer(blockWithEntity, blockRenderer);
    }

    /**
     * 获取渲染器
     * */
    public static <E extends BlockEntity> BlockEntityRenderer<E> getRenderer (E blockEntity) {
        String id = blockEntity.getProvider().getID();
        BlockEntityRenderer<? extends BlockEntity> renderer = getInstance().getRenderersMap().get(id);
        if (renderer == null) {
            Log.error(BlockEntityRendererRegistry.class.getName(), "方块：" + id + " 的渲染器不存在！！！");
        }
        return (BlockEntityRenderer<E>) renderer;
    }

    private static BlockEntityRendererRegistry INSTANCE;
    public static BlockEntityRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new BlockEntityRendererRegistry();
        }
        return INSTANCE;
    }

    private final HashMap<String, BlockEntityRenderer<? extends BlockEntity>> renderersMap = new HashMap<>();

    /**
     * 基础注册方法
     * @param blockWithEntity 传入的有方块实体的方块
     * */
    public <T extends BlockWithEntity, E extends BlockEntity>
    BlockEntityRenderer<E> registerRenderer (T blockWithEntity, BlockEntityRenderer<E> renderer) {
        if (blockWithEntity != null && renderer != null) {
            if (this.getRenderersMap().containsKey(blockWithEntity.getID())) {
                Log.print(this.getClass().getName(),
                    "已存在过方块实体：" + blockWithEntity.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            this.getRenderersMap().put(blockWithEntity.getID(), renderer);
            return renderer;
        }else {
            Log.error(this.getClass().getName(), "方块实体或者渲染器为null！！！");
        }
        return null;
    }

    public HashMap<String, BlockEntityRenderer<? extends BlockEntity>> getRenderersMap () {
        return this.renderersMap;
    }
}
