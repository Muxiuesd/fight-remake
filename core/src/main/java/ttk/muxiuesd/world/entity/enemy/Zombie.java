package ttk.muxiuesd.world.entity.enemy;

import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.state.instance.EnemyAttackTargetState;
import ttk.muxiuesd.world.entity.state.instance.EnemyRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.EnemyRestState;

/**
 * 僵尸敌人
 * <p>
 * 接近目标进行近战攻击
 * */
public class Zombie extends Enemy<Zombie> {
    public static final Vec2 DEFAULT_SIZE = new Vec2(0.8f, 0.9f);

    public Zombie (World world, EntityType<?> entityType) {
        super(world, entityType, 20, 20, 9f, 1f, 1.5f, 1.8f);
        setMeleeDamage(5f);
        setMeleeKnockback(2f);

        addState(Enemy.STATE_REST, new EnemyRestState<>());
        addState(Enemy.STATE_RANDOM_WALK, new EnemyRandomWalkState<>());
        addState(Enemy.STATE_ATTACK_TARGET, new EnemyAttackTargetState<>());

        setSize(DEFAULT_SIZE.getX(), DEFAULT_SIZE.getY());
        fastAddBodyHitBox();
    }

    @Override
    public void lazyInitialize () {
        //最开始是休息状态
        setState(Enemy.STATE_REST);
    }

    /**
     * 僵尸是近战攻击
     * */
    @Override
    public void attack (float delta, EntitySystem es) {
        this.meleeAttack(delta, es);
    }
}
