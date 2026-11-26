package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.CreatureEntity;

/**
 * 鸡
 * <p>
 * 陆生被动生物：休息与随机游走的行为由 {@link CreatureEntity} 的状态机提供，
 * 受击会本能远离攻击者（{@code CreatureEntity} 构造里已开启）。
 * <p>
 * 本类不覆写 {@code randomWalkPath}：基类默认的游走偏好（避开墙、避开 {@code Blocks.WATER}）
 * 正是陆生生物要的。河豚才需要覆写它改成趋水。
 * */
public class Chicken extends CreatureEntity<Chicken> {
    /// 碰撞箱尺寸，同时也是渲染尺寸（约为玩家的一半）
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.5f, 0.5f);

    public Chicken (World world, EntityType<? super Chicken> entityType) {
        //背包容量传 1：这是基类要求的最小合法值（Backpack 拒绝小于 1 的尺寸），鸡不使用背包（不捡拾、不持物）
        super(world, entityType, 5f, 5f, 1);
        setSize(DEFAULT_SIZE);
        //碰撞箱与尺寸同样大小
        fastAddBodyHitBox();
        setSpeed(3f);
    }
}
