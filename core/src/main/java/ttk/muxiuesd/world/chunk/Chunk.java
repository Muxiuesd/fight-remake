package ttk.muxiuesd.world.chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.ChunkTraversalJob;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.wall.Wall;


/**
 * 一个区块
 * 一行一行更新绘制
 * */
public class Chunk implements Disposable, Updateable, Drawable, ShapeRenderable {
    public final String TAG = this.getClass().getName();

    public static final int ChunkWidth = 16;
    public static final int ChunkHeight = 16;
    public static final int LowestHeight = 0;
    public static final int HighestHeight = 7;

    //区块分区
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
    private final int[][] heights;


    public Chunk(ChunkSystem chunkSystem) {
        this();
        this.chunkSystem = chunkSystem;
    }
    public Chunk () {
        this.blocks = new Block[ChunkHeight][ChunkWidth];
        this.walls  = new Wall[ChunkHeight][ChunkWidth];
        this.heights = new int[ChunkHeight][ChunkWidth];
    }

    @Override
    public void draw(Batch batch) {
        ChunkPosition cp = this.chunkPosition;
        this.traversal((x, y) -> {
            Block block = blocks[y][x];
            if (block != null) {
                block.draw(batch, x + cp.x * ChunkWidth, y + cp.y * ChunkHeight);
            }
        });
        this.traversal((x, y) -> {
            Wall<?> wall = walls[y][x];
            if (wall != null) {
                wall.draw(batch, x + cp.x * ChunkWidth, y + cp.y * ChunkHeight);
            }
        });
    }

    @Override
    public void update(float delta) {
        //TODO 需要update的特殊方块
    }

    @Override
    public void dispose() {
        this.traversal((x, y) -> {
            Block block = blocks[y][x];
            if (block != null) {
                blocks[y][x] = null;
            }

            Wall<?> wall = walls[y][x];
            if (wall != null) {
                walls[y][x] = null;
            }
        });
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

    public Wall<?> getWall(int cx, int cy) {
        return this.walls[cy][cx];
    }

    public void setWall(Wall<?> wall, int cx, int cy) {
        this.walls[cy][cx] = wall;
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
     * @param wx
     * @param wy
     * @returnp
     */
    public Block seekBlock (float wx, float wy) {
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

        return this.getBlock(cx, cy);
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        //绘制区块边界
        if (this.chunkSystem.chunkEdgeRender) {
            batch.setColor(Color.PURPLE);
            for (int i = 0; i < chunkZone.length; i++) {
                Rectangle zone = chunkZone[i];
                batch.rect(zone.x, zone.y, zone.width, zone.height);
            }
            batch.setColor(new Color(255, 0, 0, 255));
            batch.rect(
                this.chunkPosition.x * ChunkWidth,
                this.chunkPosition.y * ChunkHeight,
                ChunkWidth, ChunkHeight);
            //还原颜色
            batch.setColor(Color.WHITE);
        }
        //绘制墙体的碰撞箱范围
        if (this.chunkSystem.wallHitboxRender) {
            batch.setColor(Color.BLUE);
            this.traversal(((x, y) -> {
                Wall wall = walls[y][x];
                if (wall != null && wall.getHitbox() != null) {
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
     * @param wx
     * @param wy
     * @return
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
            chunkPosition.getX() * ChunkWidth,
            chunkPosition.getY() * ChunkHeight,
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
     * @param wx 向下取整过的世界横坐标
     * @param wy 向下取整过的世界纵坐标
     * */
    public GridPoint2 worldToChunk (float wx, float wy) {
        GridPoint2 cp = new GridPoint2();
        if (wx < 0) {
            cp.x = ChunkWidth + (int)(wx % ChunkWidth);
            cp.x %= ChunkWidth;
        }else {
            cp.x = (int) (wx % ChunkWidth);
        }
        if (wy < 0) {
            cp.y = ChunkHeight + (int)(wy % ChunkHeight);
            cp.y %= ChunkHeight;
        }else {
            cp.y = (int) (wy % ChunkHeight);
        }
        return cp;
    }

    public ChunkSystem getChunkSystem () {
        return this.chunkSystem;
    }

    public Chunk setChunkSystem (ChunkSystem chunkSystem) {
        this.chunkSystem = chunkSystem;
        return this;
    }
}
