package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.interfaces.render.world.block.WallRenderer;
import ttk.muxiuesd.registrant.BlockEntityRendererRegistry;
import ttk.muxiuesd.registrant.BlockRendererRegistry;
import ttk.muxiuesd.registrant.WallRendererRegistry;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.wall.Wall;

import java.util.ArrayList;

/**
 * 区块渲染系统
 * <p>
 * 区块的三层渲染都在这里：<b>方块层 → 墙体层 → 附着物层</b>。
 * 先把相机范围内的可见区块收集一遍，三层都只遍历这一份列表。
 * <p>
 * 带有方块实体的方块，在方块层里<b>紧接着自己的方块</b>绘制它的方块实体：
 * 方块实体是这一格方块的一部分，不是独立的一层。
 * <p>
 * 区块本身只持有数据（{@link Chunk} 不再实现 Drawable、ShapeRenderable），
 * 全工程对区块内容的绘制入口只有这个类。
 * */
public class ChunkRenderSystem extends WorldSystem implements IWorldChunkRender {
    /**
     * 渲染优先级：值越小越先。
     * <p>
     * 这个值只决定它和同一个渲染处理器里<b>其他</b>任务的先后（比如鼠标方块选框要盖在最上面），
     * 三层之间的顺序由 {@link #draw(Batch)} 里的代码顺序决定，不靠优先级区分
     * */
    public static final int RENDER_PRIORITY = 100;

    //相机范围内的可见区块，每帧复用同一个容器，避免反复新建
    private final ArrayList<Chunk> visibleChunks = new ArrayList<>();

    /**
     * 调试开关：绘制区块边界 / 绘制墙体碰撞箱
     * */
    public boolean chunkEdgeRender = false;
    public boolean wallHitboxRender = false;

    public ChunkRenderSystem (World world) {
        super(world);
    }

    /**
     * 收集相机范围内的可见区块（看不见的区块被剔除）
     * <p>
     * 剔除本身只是 N 次视锥判断（N = 已加载区块数，通常远小于绘制量），代价远低于一次绘制，
     * 所以 batchRender 与 shapeRender 各自收集一次，
     * 不去依赖「渲染管线保证先 batchRender 后 renderShape」这个隐式调用顺序
     * */
    private void collectVisibleChunks () {
        this.visibleChunks.clear();
        ChunkSystem chunkSystem = getWorld().getSystem(ChunkSystem.class);

        OrthographicCamera camera = PlayerCamera.INSTANCE.getCamera();
        //区块的世界尺寸 = 格子数 × 单格子世界尺寸（Block.WIDTH/HEIGHT = 1，故 = ChunkWidth）
        float halfW = Chunk.ChunkWidth * Block.WIDTH / 2f;
        float halfH = Chunk.ChunkHeight * Block.HEIGHT / 2f;

        for (Chunk chunk : chunkSystem.getActiveChunks()) {
            //区块左下角世界坐标（getWorldX(0) 是最左格中心，需回退半格到边界）
            float leftBottomX = chunk.getWorldX(0) - Block.WIDTH / 2f;
            float leftBottomY = chunk.getWorldY(0) - Block.HEIGHT / 2f;
            //boundsInFrustum 期望"中心点 + 半宽半高"，z 维度在 2D 正交相机中恒可见，传 0
            if (camera.frustum.boundsInFrustum(
                leftBottomX + halfW, leftBottomY + halfH, 0,
                halfW, halfH, 0
            )) {
                this.visibleChunks.add(chunk);
            }
        }
    }

    @Override
    public void batchRender (Batch batch) {
        this.draw(batch);
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public void draw (Batch batch) {
        this.collectVisibleChunks();

        //第一层：方块（带方块实体的方块，在这一层里紧接着自己的方块画它的方块实体）
        for (Chunk chunk : this.visibleChunks) this.drawBlocks(chunk, batch);
        //第二层：墙体
        for (Chunk chunk : this.visibleChunks) this.drawWalls(chunk, batch);
        //第三层：附着物
        for (Chunk chunk : this.visibleChunks) this.drawAttachments(chunk, batch);
    }

    /**
     * 绘制一个区块的方块层
     * */
    private void drawBlocks (Chunk chunk, Batch batch) {
        chunk.traversal((x, y) -> {
            Block block = chunk.getBlock(x, y);
            if (block == null) return;

            BlockRenderer<Block> renderer = BlockRendererRegistry.get(block);
            if (renderer != null) {
                BlockRenderer.Context context = renderer.getContext();
                context.x = chunk.getWorldX(x);
                context.y = chunk.getWorldY(y);
                //上下文必须成对释放：render 抛异常也不能把已取出的实例漏在池外（P-B3）
                try {
                    renderer.render(batch, block, context);
                } finally {
                    renderer.freeContext(context);
                }
            }

            //带方块实体的方块：紧接着渲染它的方块实体
            if (block instanceof BlockWithEntity blockWithEntity) {
                this.drawBlockEntity(blockWithEntity, batch);
            }
        });
    }

    /**
     * 绘制一个区块的墙体层
     * */
    private void drawWalls (Chunk chunk, Batch batch) {
        chunk.traversal((x, y) -> {
            Wall<?> wall = chunk.getWall(x, y);
            if (wall == null) return;

            WallRenderer<Wall<?>> renderer = WallRendererRegistry.getRenderer(wall);
            if (renderer != null) {
                WallRenderer.Context context = renderer.getContext();
                context.x = chunk.getWorldX(x);
                context.y = chunk.getWorldY(y);
                try {
                    renderer.render(batch, wall, context);
                } finally {
                    renderer.freeContext(context);
                }
            }
        });
    }

    /**
     * 绘制一个区块的附着物层
     * */
    private void drawAttachments (Chunk chunk, Batch batch) {
        chunk.traversal((x, y) -> {
            Attachment attachment = chunk.getAttachment(x, y);
            if (attachment == null) return;

            //附着物是方块的一种，同样走方块渲染器（贴图按生长阶段取）
            BlockRenderer<Block> renderer = BlockRendererRegistry.get(attachment);
            if (renderer != null) {
                BlockRenderer.Context context = renderer.getContext();
                context.x = chunk.getWorldX(x);
                context.y = chunk.getWorldY(y);
                try {
                    renderer.render(batch, attachment, context);
                } finally {
                    renderer.freeContext(context);
                }
            }
        });
    }

    /**
     * 绘制一个方块实体的内容（槽位里的物品等）
     * */
    private void drawBlockEntity (BlockWithEntity blockWithEntity, Batch batch) {
        BlockEntity blockEntity = blockWithEntity.getBlockEntity();
        //找对应的渲染器来执行渲染
        BlockEntityRenderer<BlockEntity> renderer = BlockEntityRendererRegistry.get(blockEntity);
        //渲染器未注册（返回null，注册表内部已log）时不绘制该方块实体，避免崩溃
        if (renderer == null) return;

        BlockPos pos = blockEntity.getBlockPos();
        BlockEntityRenderer.Context context = renderer.getContext();
        context.x = pos.x;
        context.y = pos.y;
        //上下文必须成对释放：render 抛异常也不能把已取出的实例漏在池外（P-B3）
        try {
            renderer.render(batch, blockEntity, context);
        } finally {
            renderer.freeContext(context);
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        this.collectVisibleChunks();

        for (Chunk chunk : this.visibleChunks) {
            //绘制区块边界（调试）
            if (this.chunkEdgeRender) this.renderChunkEdge(chunk, batch);
            //绘制墙体的碰撞箱范围（调试）
            if (this.wallHitboxRender) this.renderWallHitbox(chunk, batch);
        }
    }

    /**
     * 绘制一个区块的边界与 3×3 分区（调试）
     * */
    private void renderChunkEdge (Chunk chunk, ShapeRenderer batch) {
        batch.setColor(Color.PURPLE);
        //内部区域边界（区块的 3×3 分区）
        for (Rectangle zone : chunk.chunkZone) {
            batch.rect(zone.x, zone.y, zone.width, zone.height);
        }
        //区块整个边界
        batch.setColor(Color.RED);
        ChunkPosition cp = chunk.getChunkPosition();
        batch.rect(
            cp.x * Chunk.ChunkWidth + Chunk.ZONE_RECTANGLE_OFFSET.x,
            cp.y * Chunk.ChunkHeight + Chunk.ZONE_RECTANGLE_OFFSET.y,
            Chunk.ChunkWidth, Chunk.ChunkHeight
        );
        //还原颜色
        batch.setColor(Color.WHITE);
    }

    /**
     * 绘制一个区块里所有墙体的碰撞箱范围（调试）
     * */
    private void renderWallHitbox (Chunk chunk, ShapeRenderer batch) {
        batch.setColor(Color.BLUE);
        chunk.traversal((x, y) -> {
            Wall<?> wall = chunk.getWall(x, y);
            if (wall != null && wall.getHitboxRectangle() != null) {
                wall.renderShape(batch);
            }
        });
        //还原颜色
        batch.setColor(Color.WHITE);
    }

    @Override
    public int getRenderPriority () {
        return RENDER_PRIORITY;
    }
}
