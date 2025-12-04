package ttk.muxiuesd.registrant;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;

/**
 * 方块实体渲染器注册
 * */
public class BlockEntityRendererRegistry extends Registries.DefaultRegistry<BlockEntityRenderer<? extends BlockEntity>> {
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
    public static <E extends BlockEntity> BlockEntityRenderer<E> get (E blockEntity) {
        String id = blockEntity.getProvider().getID();
        BlockEntityRenderer<? extends BlockEntity> renderer = getInstance().get(id);
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

    /**
     * 基础注册方法
     * @param blockWithEntity 传入的有方块实体的方块
     * */
    public <T extends BlockWithEntity, E extends BlockEntity>
    BlockEntityRenderer<E> registerRenderer (T blockWithEntity, BlockEntityRenderer<E> renderer) {
        if (blockWithEntity != null && renderer != null) {
            Identifier identifier = new Identifier(blockWithEntity.getID());
            if (contains(identifier)) {
                Log.print(this.getClass().getName(),
                    "已存在过方块实体：" + blockWithEntity.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            register(identifier, renderer);
            return renderer;
        }else {
            Log.error(this.getClass().getName(), "方块实体或者渲染器为null！！！");
            throw new IllegalArgumentException();
        }
    }
}
