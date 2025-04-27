package ttk.muxiuesd.world.entity.common;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 鱼钩实体
 * */
public class EntityFishingHook extends Entity {
    private LivingEntity owner;
    private Direction direction;
    private Timer moveTimer;

    public EntityFishingHook() {
        initialize(Group.player);
        setSpeed(0);
        setSize(0.7f, 0.7f);
        bodyTexture = getTextureRegion(Fight.getId("fishing_hook"), "fish/fishing_hook.png");
        this.moveTimer = new Timer(0.7f);
    }

    @Override
    public void update (float delta) {
        if (this.moveTimer != null && !this.moveTimer.isReady()) {
            this.move(delta);
            this.moveTimer.update(delta);
        }else {
            //移动计时器使用完了就丢掉
            this.moveTimer = null;
        }
        super.update(delta);
    }

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
        return direction;
    }

    public void setDirection (Direction direction) {
        this.direction = direction;
    }

    public void removeSelf () {
        if (getEntitySystem() == null) return;
        getEntitySystem().remove(this);
    }
}
