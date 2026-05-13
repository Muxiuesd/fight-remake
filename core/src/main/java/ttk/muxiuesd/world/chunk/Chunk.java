package ttk.muxiuesd.world.chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.app.interfaces.render.Drawable;
import game.muxiuesd.bedrockcore.app.interfaces.render.ShapeRenderable;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.interfaces.ChunkTraversalJob;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.interfaces.render.world.block.WallRenderer;
import ttk.muxiuesd.registrant.BlockRendererRegistry;
import ttk.muxiuesd.registrant.WallRendererRegistry;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.wall.Wall;


/**
 * 一个区块
 * <p>
 * 一行一行更新绘制
 * */
public class Chunk implements Disposable, Updateable, Drawable, ShapeRenderable {
    /**
     * 将传入的世界坐标转换为这个区块里的方块数组坐标
     * @param rwx 四舍五入过的世界横坐标
     * @param rwy 四舍五入过的世界纵坐标
     * */
    public static GridPoint2 worldToChunk (float rwx, float rwy) {
        GridPoint2 cp = new GridPoint2();
        if (rwx < 0) {
            cp.x = ChunkWidth + (int)(rwx % ChunkWidth);
            cp.x %= ChunkWidth;
        }else {
            cp.x = (int) (rwx % ChunkWidth);
        }
        if (rwy < 0) {
            cp.y = ChunkHeight + (int)(rwy % ChunkHeight);
            cp.y %= ChunkHeight;
        }else {
            cp.y = (int) (rwy % ChunkHeight);
        }
        return cp;
    }


    public final String TAG = this.getClass().getName();

    public static final int ChunkWidth = 16;
    public static final int ChunkHeight = 16;
    public static final int LowestHeight = 0;
    public static final int HighestHeight = 7;

    //区块分区
    public static final Vector2 ZONE_RECTANGLE_OFFSET = new Vector2(- Block.WIDTH / 2, - Block.HEIGHT / 2);
    public static final int NotInChunk = -1;
    public static final int LeftUp   = 6, Up     = 7, RightUp   = 8;
    public static final int Left     = 3, Center = 4, Right     = 5;
    public static final int LeftDown = 0, Down   = 1, RightDown = 2;
    public Rectangle[] chunkZone;


    private ChunkSystem chunkSystem;

    //区块的坐标编号
    private ChunkPosition chunkPosition;

    //储存一个区块里的方块
    private final Block[][] blocks;
    //储存一个区块里的墙，有的位置可能为null
    private final Wall<?>[][]  walls;
    private final Botany[][]  botanys;
    private final int[][] heights;


    public Chunk(ChunkSystem chunkSystem) {
        this();
        this.chunkSystem = chunkSystem;
    }
    public Chunk () {
        this.blocks = new Block[ChunkHeight][ChunkWidth];
        this.walls  = new Wall[ChunkHeight][ChunkWidth];
        this.botanys = new Botany[ChunkHeight][ChunkWidth];
        this.heights = new int[ChunkHeight][ChunkWidth];
    }

    @Override
    public void draw(Batch batch) {
        ChunkPosition cp = this.chunkPosition;
        //绘制方快
        this.traversal((x, y) -> {
            Block block = this.blocks[y][x];
            if (block != null) {
                BlockRenderer<Block> renderer = BlockRendererRegistry.get(block);
                if (renderer != null) {
                    BlockRenderer.Context context = renderer.getContext();
                    context.x = x + cp.x * ChunkWidth;
                    context.y = y + cp.y * ChunkHeight;
                    renderer.render(batch, block, context);
                    renderer.freeContext(context);
                }
            }
        });
        //绘制墙体
        this.traversal((x, y) -> {
            Wall<?> wall = this.walls[y][x];
            if (wall != null) {
                WallRenderer<Wall<?>> renderer = WallRendererRegistry.getRenderer(wall);
                if (renderer != null) {
                    WallRenderer.Context context = renderer.getContext();
                    context.x = x + cp.x * ChunkWidth;
                    context.y = y + cp.y * ChunkHeight;
                    renderer.render(batch, wall, context);
                    renderer.freeContext(context);
                }
            }
        });
        //绘制植物
        this.traversal((x, y) -> {
            Botany botany = this.botanys[y][x];
            if (botany != null) {
                BlockRenderer<Block> renderer = BlockRendererRegistry.get(botany);
                if (renderer != null) {
                    BlockRenderer.Context context = renderer.getContext();
                    context.x = x + cp.x * ChunkWidth;
                    context.y = y + cp.y * ChunkHeight;
                    renderer.render(batch, botany, context);
                    renderer.freeContext(context);
                }
            }
        });
    }

    @Override
    public void update(float delta) {
        //TODO 需要update的特殊方块
    }

    @Override
    public void dispose() {
    }

    /**
     * 在对应坐标上设置方块
     * */
    public void setBlock (Block block, int cx, int cy) {
        this.blocks[cy][cx] = block;
        //确保区块系统里存在这个方块
        if (this.chunkSystem != null) {
            this.chunkSystem.addBlock(block, this.getWorldX(cx), this.getWorldY(cy));
        }
    }

    /**
     * 获取区块中的方块
     * @param cx    方块在区块中的横坐标
     * @param cy    方块在区块中的纵坐标
     * @return
     */
    public Block getBlock (int cx, int cy) {
        return this.blocks[cy][cx];
    }

    public Wall<?> getWall (int cx, int cy) {
        return this.walls[cy][cx];
    }

    public void setWall (Wall<?> wall, int cx, int cy) {
        this.walls[cy][cx] = wall;
    }

    /**
     * 移除墙体
     * <p>
     * 需要传入的世界坐标都在这个区块里，否则不准
     */
    public Wall<?> removeWall (float wx, float wy) {
        GridPoint2 gridPoint2 = this.worldPos2ChunkPos(wx, wy);
        Wall<?> wall = this.getWall(gridPoint2.x, gridPoint2.y);
        if (wall != null) {
            this.setWall(null, gridPoint2.x, gridPoint2.y);
            return wall;
        }
        //是null就返回null;
        return null;
    }

    /**
     * 设置这个区块上的某一个植物
     * */
    public void setBotany (Botany botany, int cx, int cy) {
        this.botanys[cy][cx] = botany;
    }

    /**
     * 获取这个区块上的某一个植物
     * */
    public Botany getBotany (int cx, int cy) {
        return this.botanys[cy][cx];
    }

    /**
     * 检测这个区块上是否有植物
     * */
    public boolean hasBotany (int cx, int cy) {
        return this.botanys[cy][cx] != null;
    }


    public void setHeight (int cx, int cy, int height) {
        if (height < LowestHeight || height > HighestHeight) {
            throw new IllegalArgumentException("传入的高度："+ height +" 不合法！！！");
        }
        this.heights[cy][cx] = height;
    }

    public int getHeight (int cx, int cy) {
        return this.heights[cy][cx];
    }

    /**
     * 查找方块
     * @param wx 世界坐标x
     * @param wy 世界坐标y
     * @return 方块
     */
    public Block seekBlock (float wx, float wy) {
        GridPoint2 chunkPos = this.worldPos2ChunkPos(wx, wy);
        return this.getBlock(chunkPos.x, chunkPos.y);
    }

    /**
     * 查找墙体
     * @param wx 世界坐标x
     * @param wy 世界坐标y
     * @return 墙体或者null
     */
    public Wall<?> seekWall (float wx, float wy) {
        GridPoint2 chunkPos = this.worldPos2ChunkPos(wx, wy);
        return this.getWall(chunkPos.x, chunkPos.y);
    }

    /**
     * 查找植物
     * @param wx 世界坐标x
     * @param wy 世界坐标y
     * @return 找到植物就返回对应的实例，否则为null
     */
    public Botany seekBotany (float wx, float wy) {
        GridPoint2 chunkPos = this.worldPos2ChunkPos(wx, wy);
        return this.getBotany(chunkPos.x, chunkPos.y);
    }

    /**
     * 世界坐标转换为区块内部的坐标
     * <p>
     * 需要传入的世界坐标都在这个区块里，否则不准
     */
    private GridPoint2 worldPos2ChunkPos (float wx, float wy) {
        int cx;
        int cy;
        if (wx < 0) {
            cx = ChunkWidth + (int)(wx % ChunkWidth);
            cx %= ChunkWidth;
        }else {
            cx = (int) (wx % ChunkWidth);
        }
        if (wy < 0) {
            cy = ChunkHeight + (int)(wy % ChunkHeight);
            cy %= ChunkHeight;
        }else {
            cy = (int) (wy % ChunkHeight);
        }
        return new GridPoint2(cx, cy);
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        //绘制区块边界
        if (this.chunkSystem.chunkEdgeRender) {
            batch.setColor(Color.PURPLE);
            //内部区域边界
            for (int i = 0; i < chunkZone.length; i++) {
                Rectangle zone = chunkZone[i];
                batch.rect(zone.x, zone.y, zone.width, zone.height);
            }
            //区块整个边界
            batch.setColor(new Color(255, 0, 0, 255));
            batch.rect(
                this.chunkPosition.x * ChunkWidth + ZONE_RECTANGLE_OFFSET.x,
                this.chunkPosition.y * ChunkHeight+ ZONE_RECTANGLE_OFFSET.y,
                ChunkWidth, ChunkHeight
            );
            //还原颜色
            batch.setColor(Color.WHITE);
        }
        //绘制墙体的碰撞箱范围
        if (this.chunkSystem.wallHitboxRender) {
            batch.setColor(Color.BLUE);
            this.traversal(((x, y) -> {
                Wall wall = walls[y][x];
                if (wall != null && wall.getHitboxRectangle() != null) {
                    /*Rectangle hitbox = wall.getHitbox();
                    batch.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());*/
                    wall.renderShape(batch);
                }
            }));
            //还原颜色
            batch.setColor(Color.WHITE);
        }

    }

    /**
     * 获取坐标在这个区块内的区域，首先要确保传入的坐标在这个区块内
     */
    public int getChunkZone (float wx, float wy) {
        ChunkPosition cp = getChunkPosition();
        //区块的起始世界坐标
        float startX = cp.x * ChunkWidth;
        float startY = cp.y * ChunkHeight;

        if (!new Rectangle(startX, startY, ChunkWidth, ChunkHeight).contains(wx, wy)) {
            Log.error(TAG, "传入的坐标(" + wx + "," + wy +")不在区块" + getChunkPosition().toString() + "内！！！");
            Log.error(TAG, "区块" + getChunkPosition().toString() + ", x:"+startX+
                ", y:"+startY+", w:"+ChunkWidth+", h:"+ChunkHeight);
            return NotInChunk;
        }

        for (int i = 0; i < 9; i++) {
            if (this.chunkZone[i].contains(wx, wy)) {
                return i;
            }
        }
        //执行到这里说明传入的坐标很奇怪，我觉得不可能执行到这里
        return NotInChunk;
    }

    /**
     * @param cx 方块在区块里的横坐标
     * @return  方块的世界坐标
     */
    public float getWorldX (int cx) {
        return this.getChunkPosition().getX() * ChunkWidth + cx;
    }

    /**
     *
     * @param cy 方块在区块里的纵坐标
     * @return
     */
    public float getWorldY (int cy) {
        return this.getChunkPosition().getY() * ChunkHeight + cy;
    }

    public void setChunkPosition(ChunkPosition chunkPosition) {
        this.chunkPosition = chunkPosition;
        this.updateChunkZone();
    }

    public ChunkPosition getChunkPosition() {
        return this.chunkPosition;
    }

    /**
     *  更新chunkZone
     */
    private void updateChunkZone () {
        this.chunkZone = new Rectangle[9];
        chunkZone[LeftDown] = new Rectangle(
            chunkPosition.getX() * ChunkWidth + ZONE_RECTANGLE_OFFSET.x,
            chunkPosition.getY() * ChunkHeight + ZONE_RECTANGLE_OFFSET.y,
            5f, 5f);
        chunkZone[Down] = new Rectangle(
            chunkZone[LeftDown].getX() + chunkZone[LeftDown].getWidth(),
            chunkZone[LeftDown].getY(),
            6f, 5f);
        chunkZone[RightDown] = new Rectangle(
            chunkZone[Down].getX() + chunkZone[Down].getWidth(),
            chunkZone[Down].getY(),
            5f, 5f);
        chunkZone[Left] = new Rectangle(
            chunkZone[LeftDown].getX(),
            chunkZone[LeftDown].getY() + chunkZone[LeftDown].getHeight(),
            5f, 6f);
        chunkZone[Center] = new Rectangle(
            chunkZone[Left].getX() + chunkZone[Left].getWidth(),
            chunkZone[Left].getY(),
            6f, 6f);
        chunkZone[Right] = new Rectangle(
            chunkZone[Center].getX() + chunkZone[Center].getWidth(),
            chunkZone[Center].getY(),
            5f, 6f);
        chunkZone[LeftUp] = new Rectangle(
            chunkZone[Left].getX(),
            chunkZone[Left].getY() + chunkZone[Left].getHeight(),
            5f, 5f);
        chunkZone[Up] = new Rectangle(
            chunkZone[LeftUp].getX() + chunkZone[LeftUp].getWidth(),
            chunkZone[LeftUp].getY(),
            6f, 5f);
        chunkZone[RightUp] = new Rectangle(
            chunkZone[Up].getX() + chunkZone[Up].getWidth(),
            chunkZone[Up].getY(),
            5f, 5f);
    }

    /**
     * 区块内遍历的快捷方法
     * */
    public void traversal (ChunkTraversalJob job) {
        for (int y = 0; y < ChunkHeight; y++) {
            for (int x = 0; x < ChunkWidth; x++) {
                job.execute(x, y);
            }
        }
    }

    /**
     * 将传入的世界坐标转换为这个区块里的方块数组坐标
     * */
    /*public GridPoint2 worldToChunk (float rwx, float rwy) {
        GridPoint2 cp = new GridPoint2();
        if (rwx < 0) {
            cp.x = ChunkWidth + (int)(rwx % ChunkWidth);
            cp.x %= ChunkWidth;
        }else {
            cp.x = (int) (rwx % ChunkWidth);
        }
        if (rwy < 0) {
            cp.y = ChunkHeight + (int)(rwy % ChunkHeight);
            cp.y %= ChunkHeight;
        }else {
            cp.y = (int) (rwy % ChunkHeight);
        }
        return cp;
    }*/

    public ChunkSystem getChunkSystem () {
        return this.chunkSystem;
    }

    public Chunk setChunkSystem (ChunkSystem chunkSystem) {
        this.chunkSystem = chunkSystem;
        return this;
    }
}
