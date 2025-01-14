package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.Bullet;

/**
 * 子弹碰撞系统
 * */
public class BulletCollisionCheckSystem extends WorldSystem {
    public BulletCollisionCheckSystem(World world) {
        super(world);
    }

    @Override
    public void update(float delta) {
        EntitySystem es = (EntitySystem) getWorld().getSystemManager().getSystem("EntitySystem");
        PlayerSystem ps = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");
        Player player = ps.getPlayer();

        if (es == null || player == null) {
            return;
        }
        // 玩家存活才检测所有碰撞！！！
        if (player.isDeath()) {
            return;
        }

        // 敌人与子弹碰撞检测
        for (Bullet playerBullet : es.playerBulletEntity) {
            if (playerBullet.getLiveTime() > playerBullet.getMaxLiveTime()) {
                es.remove(playerBullet);
                continue;
            }
            for (Entity enemy : es.enemyEntity) {
                if (playerBullet.hurtbox.overlaps(enemy.hurtbox)
                    || enemy.hurtbox.overlaps(playerBullet.hurtbox)) {
                    enemy.curHealth -= playerBullet.damage;
                    enemy.attacked = true;
                    playerBullet.setLiveTime(playerBullet.getMaxLiveTime());
                    // Log.print(TAG, "敌人扣血：" + playerBullet.damage + " 目前血量：" + enemy.curHealth);
                }
            }
        }
        // 玩家与子弹碰撞检测
        for (Bullet enemyBullet : es.enemyBulletEntity) {
            if (enemyBullet.getLiveTime() > enemyBullet.getMaxLiveTime()) {
                es.remove(enemyBullet);
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
                // Log.print(TAG, "玩家扣血：" + enemyBullet.damage + " 目前血量：" + player.curHealth);
            }
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }
}
