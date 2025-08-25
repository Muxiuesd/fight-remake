package ttk.muxiuesd.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * GUI的相机
 * */
public class GUICamera extends CameraController{
    public static GUICamera INSTANCE = new GUICamera();

    private GUICamera () {
        super(new OrthographicCamera(), 64f, 64f);
        setPosition(0f, 0f);
    }
}
