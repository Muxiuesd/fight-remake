package ttk.muxiuesd.util;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.math.Vec2;

/**
 * 方向矢量
 * <p>
 * 确保是单位向量
 */
public class Direction {
    private float x;
    private float y;
    /**
     * 默认方向：当前游戏窗口中心到鼠标的方向
     */
    public Direction () {
        Vector2 mouse = Util.getMouseWindowPos();
        float length = (float) Math.sqrt(mouse.x * mouse.x + mouse.y * mouse.y);
        if (length < 0.0001f) {
            // 鼠标在窗口中心：零向量，设为无方向
            this.x = 0;
            this.y = 0;
            return;
        }
        this.x = mouse.x / length;
        this.y = mouse.y / length;
    }

    public Direction (Vector2 from, Vector2 to) {
        this(to.x - from.x, to.y - from.y);
    }
    public Direction (Vec2 from, Vec2 to) {
        this(to.getX() - from.getX(), to.getY() - from.getY());
    }
    public Direction (Vec2 direction) {
        this(direction.getX(), direction.getY());
    }
    public Direction (float xDirection, float yDirection) {
        this.x = xDirection;
        this.y = yDirection;
        nor();
    }

    public Vector2 toVector2 () {
        return new Vector2(this.x, this.y);
    }

    public float getX () {
        return this.x;
    }

    public void setX (float x) {
        this.x = x;
        nor();
    }

    public float getY () {
        return this.y;
    }

    public void setY (float y) {
        this.y = y;
        nor();
    }

    public void nor () {
        float length = (float) Math.sqrt(this.x * this.x + this.y * this.y);
        // 零向量防护：长度为 0 时不归一化，避免产生 NaN
        if (length < 0.0001f) {
            this.x = 0;
            this.y = 0;
            return;
        }
        this.x = this.x / length;
        this.y = this.y / length;
    }
}
