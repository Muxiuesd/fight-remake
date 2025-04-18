package ttk.muxiuesd.world.entity.bullet;


import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.world.entity.Entity;

/**
 * 子弹
 */
public abstract class Bullet extends Entity {
    public Entity owner;

    public float damage;
    public float speed;
    private float maxLiveTime;  // 最大存活时间
    private float liveTime; // 已存活时间

    public Bullet () {
    }

    public Bullet(Entity owner) {
        this.owner = owner;
        initialize(owner.group);
    }

    public float getMaxLiveTime() {
        return this.maxLiveTime;
    }

    public void setMaxLiveTime(float maxLiveTime) {
        this.maxLiveTime = maxLiveTime;
    }

    public float getLiveTime() {
        return this.liveTime;
    }

    public void setLiveTime(float liveTime) {
        this.liveTime = liveTime;
    }

    public void setDirection(float xDirection, float yDirection) {
        this.velX = xDirection;
        this.velY = yDirection;
        this.setDegrees();
    }

    /**
     * 设置旋转角度，需要已知速度方向
     */
    private void setDegrees() {
        // 调整旋转原点
        setOrigin(width / 2, height / 2);
        // 计算旋转角度
        /*double v = Math.atan2(velY, velX);
        this.rotation = (float) Math.toDegrees(v);*/
        this.rotation = MathUtils.atan2Deg(velY, velX);
    }

}
