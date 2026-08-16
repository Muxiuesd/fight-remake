package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.entity.pathfinding.WalkAbility;

/**
 * 史莱姆生成
 * */
public class SlimeGenFactory implements EnemyGenFactory<Slime> {
    /// 史莱姆的碰撞箱半径（1.5×1.5，宽高最大值的一半）
    private static final float SLIME_RADIUS = 0.75f;

    /**
     * 史莱姆生成在陆地，且按实际 1.5×1.5 碰撞箱膨胀检查（周围格无墙，避免生成后碰撞箱嵌墙）
     */
    @Override
    public boolean isValidGenPos (World world, float genX, float genY) {
        return WalkAbility.canStand(
            world.getSystem(ChunkSystem.class),
            genX, genY,
            SLIME_RADIUS,
            false    //史莱姆不可游泳
        );
    }

    @Override
    public Slime[] create (World world, float genX, float genY) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Slime[] slimes = new Slime[MathUtils.random(2, 3)];
        int created = 0;
        for (int i = 0; i < slimes.length; i++) {
            //随机偏移后的实际位置也要检查（膨胀保证碰撞箱不嵌墙），不合法则放弃这一只
            float x = genX + MathUtils.random(-1f, 1f);
            float y = genY + MathUtils.random(-1f, 1f);
            if (! WalkAbility.canStand(cs, x, y, SLIME_RADIUS, false)) continue;

            Slime slime = Entities.SLIME.create(world);
            slime.setBounds(x, y, 1.5f, 1.5f);
            slime.fastAddBodyHitBox();
            slimes[created++] = slime;
        }
        //实际生成数量不足则裁剪数组
        if (created < slimes.length) {
            Slime[] result = new Slime[created];
            System.arraycopy(slimes, 0, result, 0, created);
            return result;
        }
        return slimes;
    }
}
