package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.pathfinding.WalkAbility;

/**
 * 最基础的活体实体工厂
 * */
public interface LivingEntityGenFactory<T extends LivingEntity<?>> extends EntityGenFactory<T> {
    /**
     * 生成实体的方法，只管实体的生成，不管添加（添加操作在实体生成系统里自动完成）
     * */
    T[] create (World world, float genX, float genY);

    /**
     * 检查某个位置是否适合生成这种实体
     * <p>
     * 默认规则：区块已加载、该格没有墙体、方块可行走（水等不可走方块默认不允许生成），
     * 并按 1×1 的默认实体碰撞箱做膨胀检查（周围格无墙）。
     * 需要特殊环境的实体（如河豚需要在水里）或不同碰撞箱大小的实体应覆写此方法。
     * @return 位置合法返回 true
     * */
    default boolean isValidGenPos (World world, float genX, float genY) {
        return WalkAbility.canStand(
            world.getSystem(ttk.muxiuesd.system.ChunkSystem.class),
            genX, genY,
            0.5f,   //默认按 1×1 实体碰撞箱检查（宽高各 1，半径 0.5）
            false   //默认实体不可游泳
        );
    }
}
