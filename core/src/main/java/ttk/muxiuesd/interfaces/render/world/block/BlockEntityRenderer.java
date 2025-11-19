package ttk.muxiuesd.interfaces.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.interact.Slot;

/**
 * 方块实体的渲染器接口
 * */
public interface BlockEntityRenderer<T extends BlockEntity> {
    void render(Batch batch, T blockEntity, BlockEntityRenderer.Context context);

    default BlockEntityRenderer.Context getContext () {
        return BlockEntityRenderer.Context.POOL.obtain();
    }

    default void freeContext (BlockEntityRenderer.Context context) {
        BlockEntityRenderer.Context.POOL.free(context);
    }

    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context implements Pool.Poolable {
        //池化
        public static FightPool<BlockEntityRenderer.Context> POOL = new FightPool<>(BlockEntityRenderer.Context.class,
            new Pool<BlockEntityRenderer.Context>() {
            @Override
            protected BlockEntityRenderer.Context newObject () {
                return new BlockEntityRenderer.Context();
            }
        });

        public float
            x , y,
            width = Block.BlockWidth, height = Block.BlockHeight,
            originX = 0f, originY = 0f,
            scaleX = 1f, scaleY = 1f,
            rotation = 0f;

        public Context() {}
        public Context (float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void reset () {
            this.x = 0f;
            this.y = 0f;
            this.width = Block.BlockWidth;
            this.height = Block.BlockHeight;
            this.originX = 0f;
            this.originY = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            this.rotation = 0f;
        }
    }

    /**
     * 普通标准的方块实体渲染器
     * */
    class StandardRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>{
        @Override
        public void render (Batch batch, T blockEntity, Context context) {
            //槽位不为空就渲染
            if (!blockEntity.getSlots().isEmpty()) this.drawAllSlots(batch, blockEntity, context.x, context.y);
        }

        /**
         * 绘制所有槽位
         * */
        public void drawAllSlots (Batch batch, T blockEntity, float x, float y) {
            for (Slot slot: blockEntity.getSlots()) {
                if (slot.getItemStack() != null) {
                    drawSlot(batch, blockEntity, slot, x, y);
                }
            }
        }

        /**
         * 绘制指定的槽位
         * */
        public void drawSlot (Batch batch, T blockEntity, Slot slot, float x, float y) {
            GridPoint2 interactGridSize = blockEntity.getInteractGridSize();
            GridPoint2 startPos = slot.getStartPos();
            GridPoint2 size = slot.getSize();

            float slotX = x + (float) startPos.x / interactGridSize.x;
            float slotY = y + (float) startPos.y / interactGridSize.y;
            float slotWidth  = (float) size.x / interactGridSize.x;
            float slotHeight = (float) size.y / interactGridSize.y;
            batch.draw(slot.getItemStack().getItem().texture, slotX, slotY, slotWidth, slotHeight);
        }
    }
}
