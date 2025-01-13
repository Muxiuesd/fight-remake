package ttk.muxiuesd.world.entity.bullet;


import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;

/**
 * 子弹
 */
public abstract class Bullet extends Entity {
    public float damage;
    public float xDirection;
    public float yDirection;
    public float speed;
    private float maxLiveTime;  // 最大存活时间
    private float liveTime; // 已存活时间

    public Bullet(Group group, float maxHealth, float curHealth) {
        initialize(group, maxHealth, curHealth);
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
        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.setDegrees();
    }

    /**
     * 设置旋转角度，需要已知速度方向
     */
    private void setDegrees() {
        // 调整旋转原点
        setOrigin(width / 2, height / 2);
        // 计算旋转角度
        double v = Math.atan2(yDirection, xDirection);
        this.rotation = (float) Math.toDegrees(v);
    }

}
