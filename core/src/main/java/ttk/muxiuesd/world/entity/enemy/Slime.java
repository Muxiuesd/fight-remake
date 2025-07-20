package ttk.muxiuesd.world.entity.enemy;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.item.ItemStack;

public class Slime extends Enemy<Slime> {
    public int generation;  //史莱姆的代数，用于控制史莱姆的分裂次数，分裂次数越多，代数越高
    public float factor = 0.7f;    //分裂时的缩放因子

    public Slime () {
        this(1);
    }
    public Slime(int generation) {
        super(EntityTypes.ENEMY,10f, 10f, 16f, 16f, 1f, 1.2f);
        this.generation = generation;
        loadBodyTextureRegion(Fight.getId("slime"), "enemy/slime.png");
        getBackpack().addItem(new ItemStack(Items.SLIME_BALL, MathUtils.random(1,3)));
        renderHandItem = false;
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
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.getId("bullet_fire"));
        bullet.setType(EntityTypes.ENEMY_BULLET);
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
