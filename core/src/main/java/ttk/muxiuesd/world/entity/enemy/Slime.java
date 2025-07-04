package ttk.muxiuesd.world.entity.enemy;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

public class Slime extends Enemy {
    public int generation;  //史莱姆的代数，用于控制史莱姆的分裂次数，分裂次数越多，代数越高
    public float factor = 0.7f;    //分裂时的缩放因子

    public Slime () {
        this(1);
    }

    public Slime(int generation) {
        super(10f, 10f, 16f, 16f, 1f, 1.2f);
        this.generation = generation;
        loadBodyTextureRegion(Fight.getId("slime"), "enemy/slime.png");

        Log.print(this.getClass().getName(), "Slime 初始化完成");
    }

    @Override
    public void updateTarget (float delta, EntitySystem es) {
        super.updateTarget(delta, es);
    }

    /**
     * @param direction 子弹的运动方向
     */
    @Override
    public Bullet createBullet (Entity owner, Direction direction) {
        //BulletFire bullet = new BulletFire(this);
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.getId("bullet_fire"));
        bullet.setOwner(owner);
        bullet.setSize(
            (float) (bullet.width * Math.pow(this.factor, this.generation)),
            (float) (bullet.height * Math.pow(this.factor, this.generation)));
        bullet.setPosition(x + (width - bullet.width)/2, y + (height - bullet.height)/2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
        bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
        return bullet;
    }
}
