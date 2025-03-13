package ttk.muxiuesd.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.Fight;

/**
 * 着色器注册
 * */
public class ShaderReg {

    public static final String DAYNIGHT_SHADER = registry(
        Fight.getId("daynight_shader"),
        "shaders/daynight/daynight.vert",
        "shaders/daynight/daynight.frag");

//    public static final String PARTICLE_SHADER = registry(
//        Fight.getId("particle_shader"),
//        "shaders/particle/particle.vert",
//        "shaders/particle/particle.frag"
//    );
    public static final String PARTICLE_2_SHADER = registry(
        Fight.getId("particle_shader"),
        "shaders/particle/particle.vert",
        "shaders/particle/particle.frag"
    );

    /**
     * 注册
     * @return 注册的id
     * */
    public static String registry (String id, String vertPath, String fragPath) {
        String vert = Gdx.files.internal(vertPath).readString();
        String frag = Gdx.files.internal(fragPath).readString();
        ShaderProgram shader = new ShaderProgram(vert, frag);
        ShaderScheduler.getInstance().registry(id, shader);
        return id;
    }
}
