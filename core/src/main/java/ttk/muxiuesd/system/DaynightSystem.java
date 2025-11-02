package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;

/**
 * 日夜系统
 * */
public class DaynightSystem extends WorldSystem {

    private OrthographicCamera camera;
    private TimeSystem timeSystem;

    public DaynightSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        //this.daynightShader = new DaynightShader(this);
        this.camera = PlayerCamera.INSTANCE.getCamera();
        this.timeSystem = getWorld().getSystem(TimeSystem.class);
    }

    public void begin (Batch batch) {

    }

    public void end () {
        //TODO
    }

    public float getGameTime () {
        return this.timeSystem.getGameTime();
    }
}
