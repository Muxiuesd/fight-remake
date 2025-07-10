package ttk.muxiuesd.system.abs;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.entity.EntityGenFactory;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 生成实体的系统
 * */
public class EntityGenSystem<T extends EntityGenFactory<?>> extends WorldSystem {
    private TimeSystem timeSystem;
    private PlayerSystem playerSystem;
    private EntitySystem entitySystem;
    private ChunkSystem chunkSystem;
    //所有的生成工厂
    private final ConcurrentHashMap<String, T> genFactories;

    public EntityGenSystem (World world) {
        super(world);
        this.genFactories = new ConcurrentHashMap<>();
    }

    @Override
    public void initialize () {
        this.timeSystem = (TimeSystem) getManager().getSystem("TimeSystem");
        this.playerSystem = (PlayerSystem) getManager().getSystem("PlayerSystem");
        this.entitySystem = (EntitySystem) getManager().getSystem("EntitySystem");
        this.chunkSystem = (ChunkSystem) getManager().getSystem("ChunkSystem");
    }

    /**
     * 添加一种生成规则
     * */
    public void addGenFactory (String id, T factory) {
        if (! Identifier.check(id)) {
            throw new IllegalArgumentException("输入的id：" + id + " 不合法！！！");
        }
        this.getGenFactories().put(id, factory);
    }

    /**
     * 移除一种生成规则
     * */
    public T removeGenFactory (String id) {
        if (this.getGenFactories().containsKey(id)) {
            return this.getGenFactories().remove(id);
        }
        Log.error(tag(), "输入的工厂id：" + id + " 不存在！");
        return null;
    }

    public ConcurrentHashMap<String, T> getGenFactories () {
        return this.genFactories;
    }

    public TimeSystem getTimeSystem () {
        return timeSystem;
    }

    public EntityGenSystem<T> setTimeSystem (TimeSystem timeSystem) {
        this.timeSystem = timeSystem;
        return this;
    }

    public ChunkSystem getChunkSystem () {
        return chunkSystem;
    }

    public EntityGenSystem<T> setChunkSystem (ChunkSystem chunkSystem) {
        this.chunkSystem = chunkSystem;
        return this;
    }

    public EntitySystem getEntitySystem () {
        return entitySystem;
    }

    public EntityGenSystem<T> setEntitySystem (EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
        return this;
    }

    public PlayerSystem getPlayerSystem () {
        return playerSystem;
    }

    public EntityGenSystem<T> setPlayerSystem (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;
        return this;
    }
}
