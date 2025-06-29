package ttk.muxiuesd.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * 相机管理
 * */
public class CameraController {
    public float viewportWidth = 16f;
    public float viewportHeight = 16f;
    public OrthographicCamera camera;

    public CameraController(OrthographicCamera camera) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, this.viewportWidth, this.viewportHeight * (w / h));
        this.camera = camera;
    }

    public void setPosition(float x, float y) {
        this.camera.position.set(x, y, 1.5f);
        //this.camera.zoom = 1.0f;
        this.camera.update();
    }

    public void resize(int width, int height) {
        if (width >= height) {
            this.camera.viewportWidth = this.viewportWidth ;
            this.camera.viewportHeight = this.viewportHeight * height / width;
        }else {
            this.camera.viewportWidth = this.viewportWidth * width / height;
            this.camera.viewportHeight = this.viewportHeight ;
        }
        this.camera.update();
    }
}
