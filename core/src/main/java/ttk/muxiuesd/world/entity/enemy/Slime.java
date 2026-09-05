package ttk.muxiuesd.world.entity.enemy;

import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.serialization.codecs.builders.EntityCodecBuilder;
import ttk.muxiuesd.serialization.codecs.builders.LivingEntityCodecBuilder;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatInt;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.state.instance.SlimeAttackTargetState;
import ttk.muxiuesd.world.entity.state.instance.SlimeRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.SlimeRestState;

/**
 * 敌人：史莱姆
 * */
public class Slime extends Enemy<Slime> {
    public static final String STATE_REST = Fight.ID("rest");
    public static final String STATE_RANDOM_WALK = Fight.ID("random_walk");
    public static final String STATE_ATTACK_TARGET = Fight.ID("attack_target");

    /**
     * 史莱姆的现代化编解码器
     * <p>
     * 在活物实体的字段之上又声明了史莱姆自身的字段（代数）
     */
    public static final Codec<Slime> CODEC = LivingEntityCodecBuilder.<Slime>create()
        .factory(EntityCodecBuilder::createEntity);


    public int generation;  //史莱姆的代数，用于控制史莱姆的分裂次数，分裂次数越多，代数越高
    public float factor = 0.7f;    //分裂时的缩放因子

    public Slime (World world, EntityType<? super Slime> entityType) {
        this(world, entityType, 1);
    }
    public Slime (World world, EntityType<? super Slime> entityType, int generation) {
        super(world, entityType, 10, 10, 10 ,10 , 1, 1.5f);

        this.generation = generation;

        //getBackpack().addItem(new ItemStack(Items.SLIME_BALL, MathUtils.random(1,3)));
        renderHandItem = false;

        addState(STATE_REST, new SlimeRestState());
        addState(STATE_RANDOM_WALK, new SlimeRandomWalkState());
        addState(STATE_ATTACK_TARGET, new SlimeAttackTargetState());

        fastAddBodyHitBox();
    }

    @Override
    public void lazyInitialize () {
        //最开始是休息状态
        setState(STATE_REST);
    }

    @Override
    public void readCatData (CatsHolder holder) {
        super.readCatData(holder);
        this.generation = holder.getInt("generation", 1);
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder.put("generation", new CatInt(this.generation));
    }

    public int getGeneration () {
        return this.generation;
    }

    public Slime setGeneration (int generation) {
        this.generation = generation;
        return this;
    }

    /**
     * @param direction 子弹的运动方向
     */
    @Override
    public Bullet<?> createBullet (Entity<?> owner, Direction direction) {
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.ID("bullet_fire"), owner.getEntitySystem());
        bullet.setType(EntityTypes.ENEMY_BULLET);
        bullet.setOwner(owner);
        bullet.setSize(
            (float) (bullet.getWidth() * Math.pow(this.factor, this.generation)),
            (float) (bullet.getHeight() * Math.pow(this.factor, this.generation))
        );
        //实体坐标与子弹坐标都是中心坐标，直接对齐发射
        bullet.setPosition(getX(), getY());
        bullet.setVelocity(direction, bullet.getSpeed());

        bullet.fastAddBodyHitBox();
        return bullet;
    }
}
