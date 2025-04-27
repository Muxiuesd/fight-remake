package ttk.muxiuesd.util;

import com.badlogic.gdx.math.Vector2;

/**
 * 方向
 * 单位向量
 */
public class Direction extends Vector2 {
    /**
     * 默认方向：当前游戏窗口中心到鼠标的方向
     */
    public Direction() {
        Vector2 mouse = Util.getMousePosition();
        float mouseX = mouse.x;
        float mouseY = mouse.y;
        float length = (float) Math.sqrt(Math.pow((mouseX), 2) + Math.pow((mouseY), 2));
        x = (mouseX) / length;
        y = (mouseY) / length;
    }

    public Direction(float xDirection, float yDirection) {
        float length = (float) Math.sqrt(Math.pow(xDirection, 2) + Math.pow(yDirection, 2));
        x = xDirection / length;
        y = yDirection / length;
    }

    public float getxDirection() {
        return x;
    }

    public void setxDirection(float x) {
        this.x = x;
    }

    public float getyDirection() {
        return this.y;
    }

    public void setyDirection(float y) {
        this.y = y;
    }
}
