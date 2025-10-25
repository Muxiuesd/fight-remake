package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.system.abs.EntityGenSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.genfactory.SlimeGenFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 怪物生成系统
 * */
public class MonsterGenerationSystem extends EntityGenSystem<EnemyGenFactory<?>> implements Runnable {
    public String TAG = this.getClass().getName();

    private float maxGenSpan = 8f;      //生成怪物时间间隔，现实秒
    private TaskTimer genTimer; //生成怪物计时器，到时间自动执行


    public MonsterGenerationSystem (World world) {
        super(world, new ConcurrentHashMap<>(), 12, 18);
        this.genTimer = new TaskTimer(this.maxGenSpan, this);
    }

    @Override
    public void initialize () {
        super.initialize();
        PlayerSystem ps = getPlayerSystem();
        EntitySystem es = getEntitySystem();

        /*for (int i = 0; i < 5; i++) {
            EntityTarget fish = Entities.TARGET.create(getWorld());
            fish.setBounds(ps.getPlayer().x + 5, ps.getPlayer().y - 2 + i, 1, 1);
            fish.setEntitySystem(es);
            es.add(fish);
        }*/


        /*Slime slime = Entities.SLIME.create(getWorld());
        slime.setBounds(ps.getPlayer().x + 10, ps.getPlayer().y + 10, 1, 1);
        es.add(slime);*/

        this.addGenFactory(Fight.ID("slime"), new SlimeGenFactory());
    }

    @Override
    public void update (float delta) {
        //非晚上不刷怪
        if (!getTimeSystem().isNight()) {
            return;
        }

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
        Vector2 playerCenter = player.getCenter();

        //对每一个生成工厂执行一次生成，具体生成取决于工厂接口的实现
        for (EnemyGenFactory<?> factory : getGenFactories().values()) {
            float randomRange = MathUtils.random(getMinGenRange(), getMaxGenRange());
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
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
}









