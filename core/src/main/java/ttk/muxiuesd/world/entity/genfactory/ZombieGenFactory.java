package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.enemy.Zombie;
import ttk.muxiuesd.world.entity.pathfinding.WalkAbility;

/**
 * 僵尸生成
 * */
public class ZombieGenFactory implements EnemyGenFactory<Zombie> {
    /// 僵尸的碰撞箱半径
    private static final float ZOMBIE_RADIUS = Zombie.DEFAULT_SIZE.len();

    /**
     * 僵尸生成在陆地，且按实际 1×1 碰撞箱膨胀检查（周围格无墙，避免生成后碰撞箱嵌墙）
     */
    @Override
    public boolean isValidGenPos (World world, float genX, float genY) {
        return WalkAbility.canStand(
            world.getSystem(ChunkSystem.class),
            genX, genY,
            ZOMBIE_RADIUS,
            false    //僵尸不可游泳
        );
    }

    @Override
    public Zombie[] create (World world, float genX, float genY) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Zombie[] zombies = new Zombie[MathUtils.random(1, 2)];
        int created = 0;
        for (int i = 0; i < zombies.length; i++) {
            //随机偏移后的实际位置也要检查（膨胀保证碰撞箱不嵌墙），不合法则放弃这一只
            float x = genX + MathUtils.random(-1f, 1f);
            float y = genY + MathUtils.random(-1f, 1f);
            if (!WalkAbility.canStand(cs, x, y, ZOMBIE_RADIUS, false)) continue;

            Zombie zombie = Entities.ZOMBIE.create(world);
            zombie.setBounds(x, y, 1f, 1f);
            zombie.fastAddBodyHitBox();
            zombies[created++] = zombie;
        }
        //实际生成数量不足则裁剪数组
        if (created < zombies.length) {
            Zombie[] result = new Zombie[created];
            System.arraycopy(zombies, 0, result, 0, created);
            return result;
        }
        return zombies;
    }
}
