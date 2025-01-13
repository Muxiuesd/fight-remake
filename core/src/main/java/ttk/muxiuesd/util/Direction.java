package ttk.muxiuesd.util;

import com.badlogic.gdx.math.Vector2;

/**
 * 方向
 * 单位向量
 */
public class Direction {
    private float xDirection;
    private float yDirection;

    /**
     * 默认方向：当前游戏窗口中心到鼠标的方向
     */
    public Direction() {
        Vector2 mouse = Util.getMousePosition();
        float mouseX = mouse.x;
        float mouseY = mouse.y;
        float length = (float) Math.sqrt(Math.pow((mouseX), 2) + Math.pow((mouseY), 2));
        this.xDirection = (mouseX) / length;
        this.yDirection = (mouseY) / length;
    }

    public Direction(float xDirection, float yDirection) {
        float length = (float) Math.sqrt(Math.pow(xDirection, 2) + Math.pow(yDirection, 2));
        this.xDirection = xDirection / length;
        this.yDirection = yDirection / length;
    }

    public float getxDirection() {
        return this.xDirection;
    }

    public void setxDirection(float xDirection) {
        this.xDirection = xDirection;
    }

    public float getyDirection() {
        return this.yDirection;
    }

    public void setyDirection(float yDirection) {
        this.yDirection = yDirection;
    }
}
