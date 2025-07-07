package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.entity.EnemyGenFactory;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.creature.EntityTarget;
import ttk.muxiuesd.world.entity.genfactory.SlimeGenFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 怪物生成系统
 * */
public class MonsterGenerationSystem extends WorldSystem implements Runnable {
    public String TAG = this.getClass().getName();

    private TimeSystem ts;
    private PlayerSystem ps;
    private GroundEntitySystem es;
    private ChunkSystem cs;

    private float maxGenSpan = 8f;      //生成怪物时间间隔，现实秒
    private TaskTimer genTimer; //生成怪物计时器，到时间自动执行

    private float minGenRange = 12f;    //小于这个范围不生怪
    private float maxGenRange = 26f;    //大于这个范围不生怪

    private final ConcurrentHashMap<String, EnemyGenFactory<?>> genFactories;

    public MonsterGenerationSystem (World world) {
        super(world);
        this.genFactories = new ConcurrentHashMap<>();
        this.genTimer = new TaskTimer(this.maxGenSpan, this);
    }

    @Override
    public void initialize () {
        this.ts = (TimeSystem) getManager().getSystem("TimeSystem");
        this.ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        this.es = (GroundEntitySystem) getManager().getSystem("EntitySystem");
        this.cs = (ChunkSystem) getManager().getSystem("ChunkSystem");

        for (int i = 0; i < 5; i++) {
            EntityTarget fish = new EntityTarget();
            fish.setEntitySystem(this.es);
            fish.setBounds(this.ps.getPlayer().x + 3, this.ps.getPlayer().y - 2 + i, 1, 1);
            this.es.add(fish);
        }

        this.addGenFactory(Fight.getId("slime"), new SlimeGenFactory());
    }

    @Override
    public void update (float delta) {
        //非晚上不刷怪
        if (!this.ts.isNight()) {
            return;
        }

        /*Player player = this.ps.getPlayer();
        Vector2 playerCenter = player.getCenter();

        //对每一个生成工厂执行一次生成，具体生成取决于工厂接口的实现
        for (EnemyGenFactory<?> factory : this.genFactories.values()) {
            float randomRange = MathUtils.random(this.minGenRange, this.maxGenRange);
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
            Enemy[] enemies = factory.create(getWorld(), genX, genY);
            //防止没添加进实体系统，统一执行一遍
            for (Enemy e : enemies) {
                e.setEntitySystem(this.es);
                this.es.add(e);
            }
        }*/
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
        Player player = this.ps.getPlayer();
        Vector2 playerCenter = player.getCenter();

        //对每一个生成工厂执行一次生成，具体生成取决于工厂接口的实现
        for (EnemyGenFactory<?> factory : this.genFactories.values()) {
            float randomRange = MathUtils.random(this.minGenRange, this.maxGenRange);
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
            Enemy[] enemies = factory.create(getWorld(), genX, genY);
            //防止没添加进实体系统，统一执行一遍
            for (Enemy e : enemies) {
                e.setEntitySystem(this.es);
                this.es.add(e);
            }
        }
    }

    /**
     * 添加一种生成规则
     * */
    public <T extends Enemy> MonsterGenerationSystem addGenFactory (String id, EnemyGenFactory<T> factory) {
        if (!Identifier.check(id)) {
            throw new IllegalArgumentException("输入的id：" + id + " 不合法！！！");
        }
        this.getGenFactories().put(id, factory);
        return this;
    }

    /**
     * 移除一种生成规则
     * */
    public EnemyGenFactory<?> removeGenFactory (String id) {
        if (this.getGenFactories().containsKey(id)) {
            return this.getGenFactories().remove(id);
        }
        Log.error(TAG, "输入的工厂id：" + id + " 不存在！");
        return null;
    }

    public float getMaxGenSpan () {
        return this.maxGenSpan;
    }

    public void setMaxGenSpan (float maxGenSpan) {
        this.maxGenSpan = maxGenSpan;
    }

    public float getMinGenRange () {
        return this.minGenRange;
    }

    public void setMinGenRange (float minGenRange) {
        this.minGenRange = minGenRange;
    }

    public float getMaxGenRange () {
        return this.maxGenRange;
    }

    public void setMaxGenRange (float maxGenRange) {
        this.maxGenRange = maxGenRange;
    }

    public ConcurrentHashMap<String, EnemyGenFactory<?>> getGenFactories () {
        return this.genFactories;
    }


}









