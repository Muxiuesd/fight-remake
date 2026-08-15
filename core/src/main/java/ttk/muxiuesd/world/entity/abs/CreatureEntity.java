package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.state.instance.CreatureRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.CreatureRestState;

/**
 * 普通生物实体
 * <p>
 * 继承寻路实体，生物的行为：
 * <ul>
 *   <li>不会本能靠近或远离其他实体</li>
 *   <li>没有受到攻击时随机游走（休息与随机游走状态循环）</li>
 *   <li>受到攻击后本能远离攻击者（攻击者不一定是玩家）</li>
 * </ul>
 * 默认提供休息与随机游走两个状态；子类可覆写 {@link #randomWalkPath} 定义自己的游走偏好（如水生生物趋水）
 */
public abstract class CreatureEntity<T extends CreatureEntity<T>> extends PathFindingEntity<T> {
    public static final String STATE_REST = Fight.ID("rest");
    public static final String STATE_RANDOM_WALK = Fight.ID("random_walk");
    public static final int MAX_RANDOM_COUNT = 5;   //随机游走的最大尝试次数

    private Vector2 walkDistance;   //当前随机游走的位移矢量

    public CreatureEntity (World world, EntityType<?> entityType) {
        this(world, entityType, 5, 5, 1);
    }
    public CreatureEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth) {
        this(world, entityType, maxHealth, curHealth, 1);
    }
    public CreatureEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth, int backpackSize) {
        super(world, entityType, maxHealth, curHealth, backpackSize);
        //生物受击后本能远离攻击者
        setFleeWhenHurt(true);
        //默认行为：休息与随机游走（不主动靠近任何实体）
        addState(STATE_REST, new CreatureRestState<T>());
        addState(STATE_RANDOM_WALK, new CreatureRandomWalkState<T>());
    }

    @Override
    public void lazyInitialize () {
        setState(STATE_REST);
    }

    /**
     * 生成随机游走路线（陆地生物的默认偏好：避开墙与水）
     * <p>
     * 子类可覆写（如水生生物改向趋水）
     * @param maxDistance 随机游走的最大距离
     */
    public void randomWalkPath (World world, float maxDistance) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Vector2 position = this.getCenterPos();
        float dx = 0;
        float dy = 0;

        //随机生成路线，尝试避开墙与水
        for (int count = 0; count < MAX_RANDOM_COUNT; count++) {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(maxDistance / 2f, maxDistance);
            float x = distance * MathUtils.cos((float) radian);
            float y = distance * MathUtils.sin((float) radian);
            //目标点不是墙也不是水就确认路径
            if (cs.getWall(position.x + x, position.y + y) == null
                && cs.getBlock(position.x + x, position.y + y) != Blocks.WATER) {
                dx = x;
                dy = y;
                break;
            }
        }

        this.setWalkDistance(new Vector2().set(dx, dy));
    }

    public Vector2 getWalkDistance () {
        return this.walkDistance;
    }

    public CreatureEntity<T> setWalkDistance (Vector2 walkDistance) {
        this.walkDistance = walkDistance;
        return (T) this;
    }
}
