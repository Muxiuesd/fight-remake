package ttk.muxiuesd.world.entity.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

public class Slime extends Enemy {
    public int generation;  //史莱姆的代数，用于控制史莱姆的分裂次数，分裂次数越多，代数越高
    public float factor = 0.7f;    //分裂时的缩放因子

    public Slime () {
        this(1);
    }

    public Slime(int generation) {
        super(13, 10, 1);
        initialize(Group.enemy, 10, 10);

        setSize(1, 1);
        speed = 1f;
        this.generation = generation;

        AssetsLoader.getInstance().loadAsync(Fight.getId("slime"),
            Fight.getEntityTexture("enemy/slime.png"),
            Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(Fight.getId("slime"), Texture.class);
            textureRegion = new TextureRegion(texture);
        });

        Log.print(this.getClass().getName(), "Slime 初始化完成");
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void updateTarget (float delta, EntitySystem es) {
        super.updateTarget(delta, es);
        /*Player player = es.getPlayer();
        Direction direction = new Direction(player.x - x, player.y - y);
        this.x = x + direction.getxDirection() * speed * delta;
        this.y = y + direction.getyDirection() * speed * delta;*/
        //与玩家接近一定的距离才会开始攻击
        /*if (Util.getDistance(this, player) <= this.attackRange) {
            if (span >= T) {
                Bullet bullet = this.createBullet(new Direction(player.x - x, player.y - y));
                es.add(bullet);
                span = 0;
            } else {
                span += delta;
            }
        }*/
    }

    /**
     * @param direction 子弹的运动方向
     */
    @Override
    public Bullet createBullet (Direction direction) {
        BulletFire bullet = new BulletFire(this);
        bullet.setSize(
            (float) (bullet.width * Math.pow(this.factor, this.generation)),
            (float) (bullet.height * Math.pow(this.factor, this.generation)));
        bullet.setPosition(x + (width - bullet.width)/2, y + (height - bullet.height)/2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
        bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
        return bullet;
    }
}
