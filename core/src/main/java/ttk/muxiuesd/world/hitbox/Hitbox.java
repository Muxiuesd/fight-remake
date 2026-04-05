package ttk.muxiuesd.world.hitbox;

import com.badlogic.gdx.math.Vector2;

/**
 * 碰撞箱抽象类
 * */
public abstract class Hitbox {

    /**
     * 检查两个碰撞箱是否接近
     * */
    public boolean isApproach (Hitbox otherHitbox) {
        return this.getSizeRadius() + otherHitbox.getSizeRadius()
                >= this.getCenterPos().dst(otherHitbox.getCenterPos());
    }

    /**
     * 检查是否碰撞
     * */
    abstract public boolean checkCollision (Hitbox otherHitbox);

    /**
     * 获取这个碰撞箱的大小半径，针对不同的hitbox算法不同，需要具体实现以确保精确性
     * <p>
     * 假如两个碰撞箱大小半径之和小于两者的中心距离，可认为一定不会碰撞，就可以不用检测碰撞
     * */
    abstract public float getSizeRadius ();

    /**
     * 设置中心坐标，是世界坐标
     * <p>
     * 大多与实体中心坐标重合，但有可能不会
     * */
    abstract public Hitbox setCenterPos (float cx, float cy);

    /**
     * 获取中心坐标，是世界坐标
     * */
    abstract public Vector2 getCenterPos ();
}
