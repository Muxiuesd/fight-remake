package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.CreatureGenFactory;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.system.abs.EntityGenSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.genfactory.PufferFishGenFactory;
import ttk.muxiuesd.world.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 地下生物生成
 * */
public class UndergroundCreatureGenSystem extends EntityGenSystem<CreatureGenFactory<?>> implements Runnable {
    private int maxCount = 20;  //玩家周围最大的生物数量
    private float maxGenSpan = 5f;      //生成时间间隔，现实秒
    private TaskTimer genTimer; //生成计时器，到时间自动执行

    public UndergroundCreatureGenSystem (World world) {
        super(world, new ConcurrentHashMap<>(), 8, 20);
        this.genTimer = new TaskTimer(this.maxGenSpan, this);
    }

    @Override
    public void initialize () {
        super.initialize();

        addGenFactory(Fight.ID("puffer_fish"), new PufferFishGenFactory());
    }

    @Override
    public void update (float delta) {
        //非白天不刷生物
        if (!getTimeSystem().isDay()) return;
        //玩家不存在（如未初始化）不刷生物
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return;

        //附近的生物数量超过最大值不刷生物
        int entityCount = Util.entityCount(
            getEntitySystem().getEntityArray(EntityTypes.CREATURE),
            player.getCenterPos(),
            getMaxGenRange()
        );
        if (entityCount >= maxCount) return;

        if (this.genTimer != null) {
            this.genTimer.update(delta);
            this.genTimer.isReady();
        }
    }

    @Override
    public void run () {
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return;
        Vector2 playerCenter = player.getCenterPos();

        for (CreatureGenFactory<?> factory: getGenFactories().values()) {
            float randomRange = MathUtils.random(getMinGenRange(), getMaxGenRange());
            double randomRadian = Util.randomRadian();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomRadian));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomRadian));
            //生成点不合法（区块未加载/环境不符）则跳过本次生成
            if (!factory.isValidGenPos(getWorld(), genX, genY)) continue;

            LivingEntity<?>[] entities = factory.create(getWorld(), genX, genY);
            //啥也没有生成就直接跳过
            if (entities == null) continue;
            //防止没添加进实体系统，统一执行一遍。一般来说工厂里只管生成，不管添加最好
            for (LivingEntity<?> e : entities) {
                if (e == null) continue;
                getEntitySystem().add(e);
            }
        }
    }
}
