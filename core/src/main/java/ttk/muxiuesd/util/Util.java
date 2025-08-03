package ttk.muxiuesd.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.camera.PlayerCamera;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 项目的工具类
 */
public class Util {
    public static final double PI2 = Math.PI * 2;

    /**
     * 获取鼠标指向的游戏世界坐标
     * */
    public static Vector2 getMouseWorldPosition() {
        OrthographicCamera camera = PlayerCamera.INSTANCE.getCamera();
        Vector3 mp = new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), camera.position.z);
        Vector3 up = camera.unproject(mp);
        return new Vector2(up.x, up.y);
    }

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
        float xd = entity1.getCenter().x - entity2.getCenter().x;
        float yd = entity1.getCenter().y - entity2.getCenter().y;
        return (float) Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
    }

    /**
     * 计算实体与一个指定坐标的距离
     * */
    public static float getDistance (Entity entity, float x, float y) {
        float xd = entity.getCenter().x - x;
        float yd = entity.getCenter().y - y;
        return (float) Math.sqrt(Math.pow(xd, 2) + Math.pow(yd, 2));
    }

    /**
     * 计算两个坐标之间的距离
     * */
    public static float getDistance (float x1, float y1, float x2, float y2) {
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

    public static float randomAngle () {
        return (float) Math.random() * 360f;
    }

    /**
     * 取整工具
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

    /**
     * 快速向下取整
     * */
    public static Vector2 fastFloor (float x, float y) {
        return new Vector2((float) Math.floor(x), (float) Math.floor(y));
    }

    public static String position2Sting(float x, float y) {
        return "x: " + x + " y: " + y;
    }

    public static String[] splitID (String id) {
        return id.split(":");
    }

    /**
     * 计算带方向的二维矢量夹角（-180° 到 180°）
     * @param from 起始矢量
     * @param to 目标矢量
     * @return 有符号角度（正数表示逆时针）
     */
    public static float signedAngle(Vector2 from, Vector2 to) {
        // 计算叉积的模长（带符号）
        float cross = from.crs(to);
        // 计算点积
        float dot = from.dot(to);
        // 使用 atan2 计算有符号角度（弧度）
        float radians = (float) Math.atan2(cross, dot);
        return radians * MathUtils.radiansToDegrees;
    }

    /**
     * 检测一个实体组里面的哪些实体（根据实体的中心坐标）在一个给定中心、半径和角度的扇形区域里
     * @param entities 待检测的实体组
     * @param center 扇形的圆心坐标
     * @param direction 扇形的中间矢量的方向，用以规定扇形的朝向
     * @param radius 扇形的半径
     * @param angleDegrees 扇形的半角度
     * */
    public static <T extends Entity> Array<T> sectorArea (Array<T> entities,
                                                          Vector2 center, Vector2 direction,
                                                          float radius, float angleDegrees) {
        Array<T> results = new Array<>();
        for (T entity : entities) {
            Vector2 entityCenter = entity.getCenter();
            //从中心点到实体中心的矢量
            Vector2 ce = new Vector2(entityCenter.x - center.x, entityCenter.y - center.y);
            //超过扇形的半径不在
            if (ce.len() > radius) continue;

            float angleDeg = signedAngle(direction, ce);
            //System.out.println("before angleDeg: " + angleDeg);
            //超过扇形角度不在
            if (Math.abs(angleDeg) > angleDegrees) continue;

            results.add(entity);
        }
        return results;
    }

    /**
     * 检查给定半径内的实体数量
     * */
    public static <T extends Entity> int entityCount (Array<T> entities, Vector2 center, float radius) {
        int count = 0;
        for (T entity : entities) {
            Vector2 subbed = new Vector2(center).sub(entity.getCenter());
            if (subbed.len() <= radius) count++;
        }
        return count;
    }

}
