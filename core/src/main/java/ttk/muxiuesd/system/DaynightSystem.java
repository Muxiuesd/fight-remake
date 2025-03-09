package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import ttk.muxiuesd.shader.DaynightShader;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;

/**
 * 日夜系统
 * */
public class DaynightSystem extends WorldSystem {

    private DaynightShader daynightShader;
    private OrthographicCamera camera;

    public DaynightSystem (World world) {
        super(world);
        this.daynightShader = new DaynightShader();
        this.camera = getWorld().getScreen().cameraController.camera;
    }

    @Override
    public void update (float delta) {
        this.daynightShader.update(delta);
    }

    public void begin() {
        this.daynightShader.begin(getWorld().getScreen().batch, camera);
    }

    public void end() {
        this.daynightShader.end(getWorld().getScreen().batch);
    }
}
