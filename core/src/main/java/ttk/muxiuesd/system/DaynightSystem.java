package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.shader.ShaderScheduler;
import ttk.muxiuesd.shader.ShadersReg;
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

    public void begin(Batch batch) {
        ShaderProgram shader = ShaderScheduler.getInstance().begin(ShadersReg.DAYNIGHT_SHADER, batch);
        shader.setUniformMatrix("u_projTrans", camera.combined);
        //转换为0 ~ 1.0
        shader.setUniformf("u_time", this.getGameTime() / 24f);
    }

    public void end() {
        ShaderScheduler.getInstance().end(ShadersReg.DAYNIGHT_SHADER);
    }

    public float getGameTime () {
        return this.timeSystem.getGameTime();
    }
}
