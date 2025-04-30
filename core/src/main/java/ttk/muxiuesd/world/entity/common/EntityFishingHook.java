package ttk.muxiuesd.world.entity.common;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 鱼钩实体
 * */
public class EntityFishingHook extends Entity {
    private LivingEntity owner;
    private Direction direction;
    private ChunkSystem cs;
    private ParticleSystem pts;
    private Timer moveTimer;
    private Timer bubbleEmitTimer;
    private Vector2 positionOffset;
    private float cycle;


    public EntityFishingHook() {
        initialize(Group.player);
        setSpeed(0);
        setSize(0.7f, 0.7f);
        bodyTexture = getTextureRegion(Fight.getId("fishing_hook"), "fish/fishing_hook.png");

        this.moveTimer = new TaskTimer(0.7f, () -> this.moveTimer = null); //用完就丢的计时器
        this.bubbleEmitTimer = new TaskTimer(0.6f, 0.3f, () -> {
            if (this.getParticleSystem() == null) return;
            this.pts.emitParticle(Fight.getId("entity_swimming"), MathUtils.random(2, 5),
                getCenter().add(0, - getHeight() / 2),
                new Vector2(MathUtils.random(0.5f, 1.2f), 0),
                getOrigin(),
                getSize().scl(0.3f), getSize().scl(0.06f),
                getScale(), MathUtils.random(0, 360), 1.5f);
        });

        this.positionOffset = new Vector2();
    }

    @Override
    public void update (float delta) {
        if (this.moveTimer != null && !this.moveTimer.isReady()) {
            //抛钩移动
            this.move(delta);
            this.moveTimer.update(delta);
        }else {
            if (this.cs.getBlock(x, y) instanceof BlockWater) {
                //只有鱼钩在水中才上下漂浮和产生气泡粒子
                this.cycle += delta / 2;
                if (this.cycle > 1f) this.cycle -= 1f;
                this.positionOffset.set(0, MathUtils.sin(MathUtils.PI2 * this.cycle) * 0.15f);
                //鱼钩在水中产生气泡粒子
                this.bubbleEmitTimer.update(delta);
                this.bubbleEmitTimer.isReady();
            }
        }
        super.update(delta);
    }

    @Override
    public void draw (Batch batch) {
        if (bodyTexture != null) {
            batch.draw(bodyTexture, x, y + this.positionOffset.y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
    }

    /**
     * 抛钩移动
     * */
    private void move (float delta) {
        velX = direction.x * delta;
        velY = direction.y * delta;
        x += speed * velX;
        y += speed * velY;
    }

    public LivingEntity getOwner () {
        return owner;
    }

    public EntityFishingHook setOwner (LivingEntity owner) {
        this.owner = owner;
        return this;
    }

    public Direction getDirection () {
        return direction;
    }

    public EntityFishingHook setDirection (Direction direction) {
        this.direction = direction;
        return this;
    }

    public ChunkSystem getChunkSystem () {
        return cs;
    }

    public EntityFishingHook setChunkSystem (ChunkSystem cs) {
        this.cs = cs;
        return this;
    }

    public ParticleSystem getParticleSystem () {
        return pts;
    }

    public EntityFishingHook setParticleSystem (ParticleSystem pts) {
        this.pts = pts;
        return this;
    }

    public Vector2 getPositionOffset () {
        return positionOffset;
    }

    public EntityFishingHook setPositionOffset (Vector2 positionOffset) {
        this.positionOffset = positionOffset;
        return this;
    }

    /**
     * 需要移除自己的时候
     * */
    public void removeSelf () {
        if (getEntitySystem() == null) return;
        getEntitySystem().remove(this);
    }
}
