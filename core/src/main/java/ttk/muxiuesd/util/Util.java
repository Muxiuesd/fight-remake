package ttk.muxiuesd.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.world.entity.*;

/**
 * 项目的工具类
 */
public class Util {
    public static final double PI2 = Math.PI * 2;

    /**
     * 获取鼠标的位置,相对于游戏窗口的中心
     */
    public static Vector2 getMousePosition() {
        return axeTransfer(Gdx.input.getX(), Gdx.input.getY());
    }

    public static Vector2 axeTransfer(Vector2 vector2) {
        return axeTransfer(vector2.x, vector2.y);
    }

    /**
     * 坐标转换,将以窗口左下角为原点的坐标系转换为以屏幕中心为原点的笛卡尔坐标系
     *
     * @param x 原始横坐标
     * @param y 原始纵坐标
     */
    public static Vector2 axeTransfer(float x, float y) {
        float newX = x - ((float) Gdx.graphics.getWidth() / 2);
        float newY = ((float) Gdx.graphics.getHeight() / 2) - y;
        return new Vector2(newX, newY);
    }

    /**
     * 获取窗口中心到鼠标方向的单位向量
     * */
    public static Direction getDirection() {
        return new Direction();
    }

    /**
     * 获取两个实体之间的距离
     * */
    public static float getDistance (Entity entity1, Entity entity2) {
        float xd = entity1.getPosition().x - entity2.getPosition().x;
        float yd = entity1.getPosition().y - entity2.getPosition().y;
        return (float) Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
    }

    /**
     * 计算实体与一个指定坐标的距离
     * */
    public static float getDistance (Entity entity, float x, float y) {
        float xd = entity.getPosition().x - x;
        float yd = entity.getPosition().y - y;
        return (float) Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
    }

    /**
     * 计算两个坐标之间的距离
     * */
    public static float getDistance (float x1, float y1,float x2, float y2) {
        float xd = x1 - x2;
        float yd = y1 - y2;
        return (float) Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
    }

    public static double randomSin () {
        return Math.sin(randomRadian());
    }

    public static double randomCos () {
        return Math.cos(randomRadian());
    }

    /**
     * 生成随机的弧度
     * */
    public static double randomRadian () {
        return Math.random() * PI2;
    }

    /**
     * 取整工具
     * 如负值-1.2向下取整为-2，正值则四舍五入
     * */
    public static float fastRound (float value) {
        if (value < 0) {
            return (float) Math.floor(value);
            //return Math.round(value);
        }
        if (value > 0) {
            return (float) Math.floor(value);
        }
        return value;
    }

    public static String position2Sting(float x, float y) {
        return "x: " + x + " y: " + y;
    }

}
