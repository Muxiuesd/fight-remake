package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.CreatureGenFactory;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.system.abs.EntityGenSystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.genfactory.PufferFishGenFactory;

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

        //附近的生物数量超过最大值不刷生物
        int entityCount = Util.entityCount(getEntitySystem().getEntityArray(EntityTypes.CREATURE),
            getPlayerSystem().getPlayer().getCenter(),
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
        Vector2 playerCenter = player.getCenter();

        for (CreatureGenFactory<?> factory: getGenFactories().values()) {
            float randomRange = MathUtils.random(getMinGenRange(), getMaxGenRange());
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
            LivingEntity<?>[] entities = factory.create(getWorld(), genX, genY);
            //啥也没有生成就直接跳过
            if (entities == null) continue;
            //防止没添加进实体系统，统一执行一遍。一般来说工厂里只管生成，不管添加最好
            for (LivingEntity<?> e : entities) {
                if (e == null) continue;
                //e.setEntitySystem(getEntitySystem());
                getEntitySystem().add(e);
            }
        }
    }
}
