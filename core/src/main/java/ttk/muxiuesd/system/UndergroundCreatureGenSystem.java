package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.CreatureGenFactory;
import ttk.muxiuesd.system.abs.EntityGenSystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.genfactory.PufferFishGenFactory;

/**
 * 地下生物生成
 * */
public class UndergroundCreatureGenSystem extends EntityGenSystem<CreatureGenFactory<?>> implements Runnable {
    private float minGenRange = 5f;
    private float maxGenRange = 20f;
    private float maxGenSpan = 1f;      //生成时间间隔，现实秒
    private TaskTimer genTimer; //生成计时器，到时间自动执行



    public UndergroundCreatureGenSystem (World world) {
        super(world);
        this.genTimer = new TaskTimer(this.maxGenSpan, this);
    }

    @Override
    public void initialize () {
        super.initialize();

        addGenFactory(Fight.getId("puffer_fish"), new PufferFishGenFactory());
    }

    @Override
    public void update (float delta) {
        //非白天不刷生物
        if (!getTimeSystem().isDay()) return;

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
            float randomRange = MathUtils.random(this.minGenRange, this.maxGenRange);
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
            LivingEntity[] entities = factory.create(getWorld(), genX, genY);
            //啥也没有生成就直接跳过
            if (entities == null) continue;
            //防止没添加进实体系统，统一执行一遍。一般来说工厂里只管生成，不管添加最好
            for (LivingEntity e : entities) {
                if (e == null) continue;
                e.setEntitySystem(getEntitySystem());
                getEntitySystem().add(e);
            }
        }
    }
}
