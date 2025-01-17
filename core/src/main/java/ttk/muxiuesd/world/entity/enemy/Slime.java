package ttk.muxiuesd.world.entity.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

public class Slime extends Entity {
    //public TextureRegion body = new TextureRegion(new Texture("enemy/slime.png"));
    public int generation;  //史莱姆的代数，用于控制史莱姆的分裂次数，分裂次数越多，代数越高
    public float T = 0.5f;
    public float span = 0;
    public float factor = 0.7f;    //分裂时的缩放因子
    public float attackRange = 10f;

    public Slime () {
        this(1);
    }

    public Slime(int generation) {
        initialize(Group.enemy, 10, 10);

        setSize(1, 1);
        speed = 1f;
        /*setBounds((float) (EntityManager.getInstance().player.x + 5 * Math.cos(Util.randomRadian())),
                  (float) (EntityManager.getInstance().player.y + 5 * Math.sin(Util.randomRadian())),
            1, 1);*/
        this.generation = generation;

        AssetsLoader.getInstance().loadAsync("enemy/slime.png", Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().get("enemy/slime.png", Texture.class);
            textureRegion = new TextureRegion(texture);
        });
        Log.print(this.getClass().getName(), "Slime 初始化完成");
    }

    @Override
    public void update(float delta) {
        //super.update(delta);
        EntitySystem entitySystem = getEntitySystem();
        Player player = entitySystem.getPlayer();

        Direction direction = new Direction(player.x - x, player.y - y);
        this.x = x + direction.getxDirection() * speed * delta;
        this.y = y + direction.getyDirection() * speed * delta;

        //与玩家接近一定的距离才会开始攻击
        if (Util.getDistance(this, player) <= this.attackRange) {
            if (span >= T) {
                Bullet bullet = this.createBullet(new Direction(player.x - x, player.y - y));
                getEntitySystem().add(bullet);
                span = 0;
            } else {
                span += delta;
            }
        }
        super.update(delta);
    }

    /**
     * @param direction 子弹的运动方向
     */
    private Bullet createBullet (Direction direction) {
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
