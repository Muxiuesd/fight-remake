package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 实体碰撞系统
 * <p>
 * 实体与实体之间的碰撞，实体与墙体的碰撞
 * */
public class EntityCollisionSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private final EntitySystem es;
    private final PlayerSystem ps;
    private final ChunkSystem cs;

    public EntityCollisionSystem (World world) {
        super(world);
        this.es = (EntitySystem) getManager().getSystem("EntitySystem");
        this.ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        this.cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
    }

    @Override
    public void update (float delta) {
        this.playerCollisionCheck(delta);
    }

    /**
     * 玩家相关碰撞
     * */
    private void playerCollisionCheck (float delta) {
        Player player = this.es.getPlayer();
        Vector2 perPosition = player.getPosition();
        float perX = perPosition.x;
        float perY = perPosition.y;

        Vector2 movement = player.getVelocity();
        //下一步位置
        float nextX = perX + movement.x;
        float nextY = perY + movement.y;
        Rectangle nextHitbox = new Rectangle(nextX, nextY, player.getWidth(), player.getHeight());

        ChunkPosition chunkPosition = this.cs.getChunkPosition(nextX, nextY);
        Chunk chunk = cs.getChunk(chunkPosition);
        Array<Wall> collidingWalls = new Array<>();

        // X轴移动
        player.hitbox.x += movement.x;
        chunk.traversal((x, y) -> {
            Wall wall = chunk.getWall(x, y);
            if (wall != null && player.hitbox.overlaps(wall.getHitbox())) {
                resolveCollision(player, nextHitbox, wall.getHitbox(), new Vector2(movement.x, 0));
                collidingWalls.add(wall);
            }
        });

        // Y轴移动
        player.hitbox.y += movement.y;
        chunk.traversal((x, y) -> {
            Wall wall = chunk.getWall(x, y);
            if (wall != null && player.hitbox.overlaps(wall.getHitbox())) {
                resolveCollision(player, nextHitbox, wall.getHitbox(), new Vector2(0, movement.y));
                collidingWalls.add(wall);
            }
        });

        player.setPosition(nextHitbox.x, nextHitbox.y);
        //速度最后要归零，否则有惯性
        player.velX = 0;
        player.velY = 0;
    }

    private void resolveCollision(Player player, Rectangle nextHitbox, Rectangle wallRect, Vector2 moveDir) {
        Vector2 normal = getCollisionNormal(nextHitbox, wallRect);

        // 计算实际允许移动量
        float allowedX = moveDir.x * (1 - Math.abs(normal.x));
        float allowedY = moveDir.y * (1 - Math.abs(normal.y));

        nextHitbox.x -= moveDir.x - allowedX;
        nextHitbox.y -= moveDir.y - allowedY;
        /*player.x -= moveDir.x - allowedX;
        player.y -= moveDir.y - allowedY;*/

        // 速度归零
        if (normal.x != 0) player.velX = 0;
        if (normal.y != 0) player.velY = 0;
    }

    public Vector2 getCollisionNormal(Rectangle playerRect, Rectangle wallRect) {
        // 计算重叠区域
        float overlapLeft = playerRect.x + playerRect.width - wallRect.x;
        float overlapRight = wallRect.x + wallRect.width - playerRect.x;
        float overlapTop = playerRect.y + playerRect.height - wallRect.y;
        float overlapBottom = wallRect.y + wallRect.height - playerRect.y;

        // 找最小重叠方向
        float minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
            Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapLeft) return new Vector2(-1, 0);
        if (minOverlap == overlapRight) return new Vector2(1, 0);
        if (minOverlap == overlapTop) return new Vector2(0, 1);
        return new Vector2(0, -1);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        Player player = this.es.getPlayer();
        Rectangle hurtbox = player.hitbox;
        batch.rect(hurtbox.x, hurtbox.y, hurtbox.getWidth(), hurtbox.getHeight());
    }
}
