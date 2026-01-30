package ttk.muxiuesd.world.hitbox;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Pools;

/**
 * 矩形的碰撞箱
 * <p>
 * 需要指定碰撞箱的中心坐标、起点距离中心的相对坐标、终点距离中心的相对坐标
 * */
public class RectHitbox extends Hitbox {
    public static final int CENTER_X        = 0;
    public static final int CENTER_Y        = 1;
    public static final int START_X_OFFSET  = 2;
    public static final int START_Y_OFFSET  = 3;
    public static final int END_X_OFFSET    = 4;
    public static final int END_Y_OFFSET    = 5;

    private float[] hitboxData; //用数组来高效管理

    public RectHitbox () {
        this.hitboxData = new float[6];
    }

    /**
     * 检查是否碰撞的核心方法
     * */
    @Override
    public boolean checkCollision (Hitbox otherHitbox) {
        boolean result = false;
        //两者之间的距离大于碰撞箱大小之和就不用检测
        if (isApproach(otherHitbox)) {
            return false;
        }
        if (otherHitbox instanceof RectHitbox otherRectHitbox) {
            Rectangle thisRect = this.getRectangle();
            Rectangle otherRect = otherRectHitbox.getRectangle();
            result = thisRect.overlaps(otherRect);
        }

        return result;
    }

    @Override
    public float getSizeRadius () {
        float ax = (this.getStartX() + this.getEndX()) / 2;
        float ay = (this.getStartY() + this.getEndY()) / 2;
        return (float) Math.sqrt(ax * ax + ay * ay);
    }


    @Override
    public RectHitbox setCenterPos (float cx, float cy) {
        this.hitboxData[CENTER_X] = cx;
        this.hitboxData[CENTER_Y] = cy;
        return this;
    }

    @Override
    public Vector2 getCenterPos () {
        return Pools.VEC2.obtain().set(this.getCenterX(), this.getCenterY());
    }

    /**
     * 设置相对于中心的起点坐标
     * */
    public RectHitbox setStartPos (float x, float y) {
        this.hitboxData[START_X_OFFSET] = x;
        this.hitboxData[START_Y_OFFSET] = y;
        return this;
    }

    /**
     * 设置相对于中心的终点坐标
     * */
    public RectHitbox setEndPos (float x, float y) {
        this.hitboxData[END_X_OFFSET] = x;
        this.hitboxData[END_Y_OFFSET] = y;
        return this;
    }

    /**
     * 获取这个碰撞箱的矩形
     * */
    public Rectangle getRectangle () {
        float startX = this.getStartX();
        float startY = this.getStartY();
        return Pools.RECT.obtain().set(
            this.getCenterX() + startX,
            this.getCenterY() + startY,
            Math.abs(this.getEndX() - startX),
            Math.abs(this.getEndY() - startY)
        );
    }

    public float getCenterX () {
        return this.hitboxData[CENTER_X];
    }

    public float getCenterY () {
        return this.hitboxData[CENTER_Y];
    }

    public float getStartX () {
        return this.hitboxData[START_X_OFFSET];
    }

    public float getStartY () {
        return this.hitboxData[START_Y_OFFSET];
    }

    public float getEndX () {
        return this.hitboxData[END_X_OFFSET];
    }

    public float getEndY () {
        return this.hitboxData[END_Y_OFFSET];
    }
}
