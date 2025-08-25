package ttk.muxiuesd.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import ttk.muxiuesd.system.game.GUISystem;

/**
 * GUI的相机
 * */
public class GUICamera extends CameraController{
    public static GUICamera INSTANCE = new GUICamera();

    private GUICamera () {
        super(new OrthographicCamera(), 512f, 512f);
        setPosition(0f, 0f);
    }

    @Override
    public void resize (int width, int height) {
        super.resize(width, height);
        OrthographicCamera camera = getCamera();
        GUISystem.getInstance().resize(camera.viewportWidth, camera.viewportHeight);
    }
}
