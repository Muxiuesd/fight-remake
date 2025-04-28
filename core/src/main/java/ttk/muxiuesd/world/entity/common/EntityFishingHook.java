package ttk.muxiuesd.world.entity.common;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Direction;
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
    private Timer moveTimer;
    private Vector2 positionOffset;
    private float cycle;


    public EntityFishingHook() {
        initialize(Group.player);
        setSpeed(0);
        setSize(0.7f, 0.7f);
        bodyTexture = getTextureRegion(Fight.getId("fishing_hook"), "fish/fishing_hook.png");
        this.moveTimer = new Timer(0.7f);
        this.positionOffset = new Vector2();
    }

    @Override
    public void update (float delta) {
        if (this.moveTimer != null && !this.moveTimer.isReady()) {
            //抛钩移动
            this.move(delta);
            this.moveTimer.update(delta);
        }else {
            //移动计时器使用完了就丢掉
            this.moveTimer = null;

            if (this.cs.getBlock(x, y) instanceof BlockWater) {
                //只有鱼钩在水中才上下漂浮
                this.cycle += delta / 2;
                if (this.cycle > 1f) this.cycle -= 1f;
                this.positionOffset.set(0, MathUtils.sin(MathUtils.PI2 * this.cycle) * 0.15f);
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
        x += delta * speed * direction.x;
        y += delta * speed * direction.y;
    }

    public LivingEntity getOwner () {
        return this.owner;
    }

    public void setOwner (LivingEntity owner) {
        this.owner = owner;
    }

    public Direction getDirection () {
        return this.direction;
    }

    public void setDirection (Direction direction) {
        this.direction = direction;
    }

    public ChunkSystem getChunkSystem () {
        return this.cs;
    }

    public void setChunkSystem (ChunkSystem cs) {
        this.cs = cs;
    }

    public Vector2 getPositionOffset () {
        return this.positionOffset;
    }

    public void setPositionOffset (Vector2 positionOffset) {
        this.positionOffset = positionOffset;
    }

    /**
     * 需要移除自己的时候
     * */
    public void removeSelf () {
        if (getEntitySystem() == null) return;
        getEntitySystem().remove(this);
    }
}
