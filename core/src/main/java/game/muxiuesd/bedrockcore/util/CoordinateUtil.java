package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * 坐标相关的工具
 * */
public class CoordinateUtil {

    public static Vector2 axeTransfer(Vector2 vector2) {
        return CoordinateUtil.axeTransfer(vector2.x, vector2.y);
    }

    /**
     * 坐标转换,将以窗口左下角为原点的坐标系转换为以屏幕中心为原点的笛卡尔坐标系
     * @param x 原始横坐标
     * @param y 原始纵坐标
     */
    public static Vector2 axeTransfer(float x, float y) {
        float newX = x - ((float) Gdx.graphics.getWidth() / 2);
        float newY = ((float) Gdx.graphics.getHeight() / 2) - y;
        return new Vector2(newX, newY);
    }
}
