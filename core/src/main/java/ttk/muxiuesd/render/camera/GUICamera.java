package ttk.muxiuesd.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * GUI的相机
 * */
public class GUICamera extends CameraController{
    public static GUICamera INSTANCE = new GUICamera();

    private GUICamera () {
        super(new OrthographicCamera(), 512f, 512f);
    }
}
