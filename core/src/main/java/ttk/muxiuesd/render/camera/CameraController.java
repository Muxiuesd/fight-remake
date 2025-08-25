package ttk.muxiuesd.render.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * 相机控制器
 * */
public class CameraController {
    private float viewportWidth = 16f;
    private float viewportHeight = 16f;
    private OrthographicCamera camera;

    public CameraController(OrthographicCamera camera, float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        this.camera = camera;
        this.camera.setToOrtho(false, this.viewportWidth, this.viewportHeight * (w / h));
    }

    public void setPosition(float x, float y) {
        this.camera.position.set(x, y, 1.5f);
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

    public float getViewportWidth () {
        return viewportWidth;
    }

    public CameraController setViewportWidth (float viewportWidth) {
        this.viewportWidth = viewportWidth;
        return this;
    }

    public float getViewportHeight () {
        return viewportHeight;
    }

    public CameraController setViewportHeight (float viewportHeight) {
        this.viewportHeight = viewportHeight;
        return this;
    }

    public OrthographicCamera getCamera () {
        return camera;
    }

    public CameraController setCamera (OrthographicCamera camera) {
        this.camera = camera;
        return this;
    }
}
