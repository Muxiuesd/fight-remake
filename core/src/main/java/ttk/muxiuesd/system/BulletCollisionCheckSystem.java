package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.EntityAttackedEvent;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventGroup;
import ttk.muxiuesd.world.wall.Wall;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 子弹碰撞系统
 * */
public class BulletCollisionCheckSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    public BulletCollisionCheckSystem(World world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        EntitySystem es = (EntitySystem) getManager().getSystem("EntitySystem");
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        ChunkSystem  cs = (ChunkSystem) getManager().getSystem("ChunkSystem");

        Player player = ps.getPlayer();

        if (es == null || player == null || cs == null) {
            return;
        }
        // 玩家存活才检测所有碰撞！！！
        if (player.isDeath()) {
            return;
        }

        //玩家的子弹碰撞检测
        for (Bullet playerBullet : es.playerBulletEntity) {
            if (playerBullet.getLiveTime() > playerBullet.getMaxLiveTime()) {
                es.remove(playerBullet);
                continue;
            }
            //敌人与玩家子弹
            for (Entity enemy : es.enemyEntity) {
                if (playerBullet.hurtbox.overlaps(enemy.hurtbox)
                    || enemy.hurtbox.overlaps(playerBullet.hurtbox)) {
                    enemy.curHealth -= playerBullet.damage;
                    enemy.attacked = true;
                    playerBullet.setLiveTime(playerBullet.getMaxLiveTime());
                    this.callEntityAttackedEvent(playerBullet, enemy);
                    // Log.print(TAG, "敌人扣血：" + playerBullet.damage + " 目前血量：" + enemy.curHealth);
                }
            }

            this.bulletAndWallCollision(playerBullet, es, ps, cs);
        }
        // 玩家与子弹碰撞检测
        for (Bullet enemyBullet : es.enemyBulletEntity) {
            if (enemyBullet.getLiveTime() > enemyBullet.getMaxLiveTime()) {
                es.remove(enemyBullet);
                continue;
            }
            // 玩家防御
            if (player.isDefend) {
                float distance = Util.getDistance(player, enemyBullet);
                if (distance <= player.defenseRadius) {
                    es.remove(enemyBullet);
                    continue;
                }
            }

            if (enemyBullet.hurtbox.contains(player.hurtbox)
                || player.hurtbox.contains(enemyBullet.hurtbox)) {
                player.curHealth -= enemyBullet.damage;
                enemyBullet.setLiveTime(enemyBullet.getMaxLiveTime());
                player.attacked = true;
                this.callEntityAttackedEvent(enemyBullet, player);
                // Log.print(TAG, "玩家扣血：" + enemyBullet.damage + " 目前血量：" + player.curHealth);
            }
            this.bulletAndWallCollision(enemyBullet, es, ps, cs);
        }

    }

    /**
     * 子弹与墙体碰撞
     * */
    private void bulletAndWallCollision(Bullet bullet, EntitySystem es, PlayerSystem ps, ChunkSystem cs) {
        //墙体与子弹
        Vector2 bulletPosition = bullet.getPosition();
        ChunkPosition chunkPosition = cs.getChunkPosition(bulletPosition.x, bulletPosition.y);
        Chunk chunk = cs.getChunk(chunkPosition);

        if (chunk == null) {
            //TODO
            return;
        }
        //与子弹碰撞到的墙体
        ArrayList<Wall> collidingWalls = new ArrayList<>();

        //大概率会位于这个区块的边缘
        if (cs.isChunkEdge(chunkPosition, bulletPosition.x, bulletPosition.y)) {
            //先跟本区块内的墙体检测
            /*for (int cy = 0; cy < 16; cy++) {
                for (int cx = 0; cx < 16; cx++) {
                    Wall wall = chunk.getWall(cx, cy);
                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                        //碰撞了
                        es.remove(bullet);
                        //TODO 检测多重碰撞
                        break;
                    }
                }
            }*/
            chunk.traversal((cx, cy) -> {
                Wall wall = chunk.getWall(cx, cy);
                if (wall != null) {
                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                        //碰撞了，就将墙体加进去
                        collidingWalls.add(wall);
                    }
                }
            });
            //区块的哪个分区上
            int chunkZone = chunk.getChunkZone(bulletPosition.x, bulletPosition.y);
            //在边缘,就检测与相邻区块的碰撞
            if (collidingWalls.isEmpty()) {
                //依据分区来精细化与相邻区块的检测
                switch (chunkZone) {
                    case Chunk.LeftDown:{
                        Chunk leftDownChunk = cs.getChunk(chunkPosition.getX() - 1, chunkPosition.getY() - 1);
                        Chunk leftChunk = cs.getChunk(chunkPosition.getX() - 1, chunkPosition.getY());
                        Chunk downChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() - 1);
                        Chunk[] chunkGroup = new Chunk[]{
                            leftChunk, leftDownChunk, downChunk
                        };
                        for (int i = 0; i < 3; i++) {
                            if (chunkGroup[i] == null) {
                                Log.error(TAG, "待检测碰撞的区块为null！！！");
                            }
                            chunkGroup[i].traversal((cx, cy) -> {
                                Wall wall = chunk.getWall(cx, cy);
                                if (wall != null) {
                                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                        //碰撞了，就将墙体加进去
                                        collidingWalls.add(wall);
                                    }
                                }
                            });
                        }
                        break;
                    }
                    case Chunk.Down:{
                        Chunk downChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() - 1);
                        downChunk.traversal((cx, cy) -> {
                            Wall wall = chunk.getWall(cx, cy);
                            if (wall != null) {
                                if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                    //碰撞了，就将墙体加进去
                                    collidingWalls.add(wall);
                                }
                            }
                        });
                        break;
                    }
                    case Chunk.RightDown:{
                        Chunk rightDownChunk = cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY() - 1);
                        Chunk rightChunk= cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY());
                        Chunk downChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() - 1);
                        Chunk[] chunkGroup = new Chunk[]{
                            rightDownChunk, rightChunk, downChunk
                        };
                        for (int i = 0; i < 3; i++) {
                            if (chunkGroup[i] == null) {
                                Log.error(TAG, "待检测碰撞的区块为null！！！");
                            }
                            chunkGroup[i].traversal((cx, cy) -> {
                                Wall wall = chunk.getWall(cx, cy);
                                if (wall != null) {
                                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                        //碰撞了，就将墙体加进去
                                        collidingWalls.add(wall);
                                    }
                                }
                            });
                        }
                        break;
                    }
                    case Chunk.Left:{
                        Chunk leftChunk = cs.getChunk(chunkPosition.getX() - 1, chunkPosition.getY());
                        leftChunk.traversal((cx, cy) -> {
                            Wall wall = chunk.getWall(cx, cy);
                            if (wall != null) {
                                if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                    //碰撞了，就将墙体加进去
                                    collidingWalls.add(wall);
                                }
                            }
                        });
                        break;
                    }
                    case Chunk.Center:{
                        //已经检测过了，运行到这里不会在中心
                        break;
                    }
                    case Chunk.Right:{
                        Chunk rightChunk = cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY());
                        rightChunk.traversal((cx, cy) -> {
                            Wall wall = chunk.getWall(cx, cy);
                            if (wall != null) {
                                if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                    //碰撞了，就将墙体加进去
                                    collidingWalls.add(wall);
                                }
                            }
                        });
                        break;
                    }
                    case Chunk.LeftUp:{
                        Chunk liftDownChunk = cs.getChunk(chunkPosition.getX() - 1, chunkPosition.getY() - 1);
                        Chunk liftChunk= cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY());
                        Chunk upChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() + 1);
                        Chunk[] chunkGroup = new Chunk[]{
                            liftDownChunk, liftChunk, upChunk
                        };
                        for (int i = 0; i < 3; i++) {
                            if (chunkGroup[i] == null) {
                                Log.error(TAG, "待检测碰撞的区块为null！！！");
                            }
                            chunkGroup[i].traversal((cx, cy) -> {
                                Wall wall = chunk.getWall(cx, cy);
                                if (wall != null) {
                                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                        //碰撞了，就将墙体加进去
                                        collidingWalls.add(wall);
                                    }
                                }
                            });
                        }
                        break;
                    }
                    case Chunk.Up:{
                        Chunk upChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() + 1);
                        upChunk.traversal((cx, cy) -> {
                            Wall wall = chunk.getWall(cx, cy);
                            if (wall != null) {
                                if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                    //碰撞了，就将墙体加进去
                                    collidingWalls.add(wall);
                                }
                            }
                        });
                        break;
                    }
                    case Chunk.RightUp:{
                        Chunk rightUpChunk = cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY() + 1);
                        Chunk rightChunk= cs.getChunk(chunkPosition.getX() + 1, chunkPosition.getY());
                        Chunk upChunk = cs.getChunk(chunkPosition.getX(), chunkPosition.getY() + 1);
                        Chunk[] chunkGroup = new Chunk[]{
                            rightUpChunk, rightChunk, upChunk
                        };
                        for (int i = 0; i < 3; i++) {
                            if (chunkGroup[i] == null) {
                                Log.error(TAG, "待检测碰撞的区块为null！！！");
                            }
                            chunkGroup[i].traversal((cx, cy) -> {
                                Wall wall = chunk.getWall(cx, cy);
                                if (wall != null) {
                                    if (wall.getHitbox().overlaps(bullet.hurtbox)) {
                                        //碰撞了，就将墙体加进去
                                        collidingWalls.add(wall);
                                    }
                                }
                            });
                        }
                        break;
                    }
                    default:{
                        //说明值为-1
                        //TODO
                    }
                }
            }

        }else {
            //在区块的中心
        }
        //不为空就说明碰撞了
        if (!collidingWalls.isEmpty()) {
            es.remove(bullet);
        }
    }

    /**
     * 调用实体受攻击的事件
     * */
    private void callEntityAttackedEvent (Entity attackedObject, Entity victim) {
        EventGroup<EntityAttackedEvent> eventGroup = EventBus.getInstance().getEventGroup(EventBus.EntityAttacked);
        HashSet<EntityAttackedEvent> events = eventGroup.getEvents();
        for (EntityAttackedEvent event :events) {
            event.call(attackedObject, victim);
        }
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }

    @Override
    public void dispose() {

    }
}
