package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 史莱姆生成
 * */
public class SlimeGenFactory implements EnemyGenFactory<Slime> {
    @Override
    public Slime[] create (World world, float genX, float genY) {
        Slime[] slimes = new Slime[MathUtils.random(1, 3)];
        for (int i = 0; i < slimes.length; i++) {
            Slime slime = Entities.SLIME.create(world);
            float x = genX + MathUtils.random(-1f, 1f);
            float y = genY + MathUtils.random(-1f, 1f);
            slime.setBounds(x, y, 1.5f, 1.5f);

            slimes[i] = slime;
        }
        return slimes;
    }
}
