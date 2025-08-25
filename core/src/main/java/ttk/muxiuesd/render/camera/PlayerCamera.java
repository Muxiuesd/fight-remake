package ttk.muxiuesd.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * 玩家相机单例
 * */
public class PlayerCamera extends CameraController{
    public static PlayerCamera INSTANCE = new PlayerCamera();

    private PlayerCamera () {
        super(new OrthographicCamera(), 16f, 16f);
    }
}
