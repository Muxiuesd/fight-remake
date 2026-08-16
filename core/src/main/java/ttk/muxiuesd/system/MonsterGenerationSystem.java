package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.system.abs.EntityGenSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.genfactory.SlimeGenFactory;
import ttk.muxiuesd.world.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 怪物生成系统
 * */
public class MonsterGenerationSystem extends EntityGenSystem<EnemyGenFactory<?>> implements Runnable {
    public String TAG = this.getClass().getName();

    private float maxGenSpan = 8f;      //生成怪物时间间隔，现实秒
    private TaskTimer genTimer; //生成怪物计时器，到时间自动执行
    private int maxCount = 30;  //玩家周围最大的怪物数量

    public MonsterGenerationSystem (World world) {
        super(world, new ConcurrentHashMap<>(), 12, 18);
        this.genTimer = new TaskTimer(this.maxGenSpan, this);
    }

    @Override
    public void initialize () {
        super.initialize();
        this.addGenFactory(Fight.ID("slime"), new SlimeGenFactory());
    }

    @Override
    public void update (float delta) {
        //非晚上不刷怪
        if (!getTimeSystem().isNight()) {
            return;
        }
        //玩家不存在（如未初始化）不刷怪
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return;

        //附近的怪物数量超过最大值不刷怪
        int entityCount = Util.entityCount(
            getEntitySystem().getEnemyEntity(),
            player.getCenterPos(),
            getMaxGenRange()
        );
        if (entityCount >= maxCount) return;

        //更新计时器
        if (this.genTimer != null) {
            this.genTimer.update(delta);
            this.genTimer.isReady();
        }
    }

    /**
     * 这里面写生成任务
     * */
    @Override
    public void run () {
        EntitySystem es = getEntitySystem();
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return;
        Vector2 playerCenter = player.getCenterPos();

        //对每一个生成工厂执行一次生成，具体生成取决于工厂接口的实现
        for (EnemyGenFactory<?> factory : getGenFactories().values()) {
            float randomRange = MathUtils.random(getMinGenRange(), getMaxGenRange());
            double randomRadian = Util.randomRadian();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomRadian));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomRadian));
            //生成点不合法（区块未加载/墙内/不可走方块）则跳过本次生成
            if (!factory.isValidGenPos(getWorld(), genX, genY)) continue;

            Enemy<?>[] enemies = factory.create(getWorld(), genX, genY);
            //啥也没有生成就直接跳过
            if (enemies == null) continue;
            //防止没添加进实体系统，统一执行一遍
            for (Enemy<?> e : enemies) {
                if (e == null) continue;
                es.add(e);
            }
        }

        Log.print(TAG, "刷怪");
    }

    public float getMaxGenSpan () {
        return this.maxGenSpan;
    }

    public void setMaxGenSpan (float maxGenSpan) {
        this.maxGenSpan = maxGenSpan;
    }

    public int getMaxCount () {
        return this.maxCount;
    }

    public void setMaxCount (int maxCount) {
        if (maxCount >= 0) this.maxCount = maxCount;
    }
}
