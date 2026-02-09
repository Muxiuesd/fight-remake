package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * 游戏相机相关的工具
 * */
public class CameraUtil {
    /**
     * 获取鼠标的指向的相机当中的坐标
     * @param camera 游戏的相机
     * */
    public static Vector2 getMousePosForCamera(Camera camera) {
        Vector3 mp = new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), camera.position.z);
        Vector3 up = camera.unproject(mp);
        return new Vector2(up.x, up.y);
    }

    /**
     * 获取鼠标的指向的相机当中的坐标增量
     * */
    public static Vector2 getMouseDeltaPosForCamera(Camera camera) {
        Vector3 mp = new Vector3(new Vector2(Gdx.input.getDeltaX(), Gdx.input.getDeltaY()), camera.position.z);
        Vector3 up = camera.unproject(mp);
        return new Vector2(up.x, up.y);
    }
}
