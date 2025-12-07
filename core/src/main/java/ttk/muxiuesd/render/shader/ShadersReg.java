package ttk.muxiuesd.render.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.Fight;

/**
 * 着色器注册
 * */
public class ShadersReg {
    //gdx默认的着色器
    public static final String DEFAULT_SHADER = registerDefault();

    public static final String DAYNIGHT_SHADER = register(
        Fight.ID("daynight_shader"),
        "shaders/daynight/daynight.vert",
        "shaders/daynight/daynight.frag");

    public static final String PARTICLE_SHADER = register(
        Fight.ID("particle_1_shader"),
        "shaders/particle/particle.vert",
        "shaders/particle/particle.frag"
    );

    public static final String PARTICLE_2_SHADER = register(
        Fight.ID("particle_shader"),
        "shaders/particle/particle_2.vert",
        "shaders/particle/particle_2.frag"
    );

    /**
     * 注册
     * @return 注册的id
     * */
    public static String register (String id, String vertPath, String fragPath) {
        String vert = Gdx.files.internal(vertPath).readString();
        String frag = Gdx.files.internal(fragPath).readString();

        System.out.println("vert代码:");
        System.out.println(vert);
        System.out.println("frag代码:");
        System.out.println(frag);

        ShaderProgram shader = new ShaderProgram(vert, frag);
        ShaderScheduler.getInstance().registry(id, shader);
        return id;
    }

    private static String registerDefault() {
        String id = Fight.ID("default_shader");
        ShaderProgram shader = SpriteBatch.createDefaultShader();
        ShaderScheduler.getInstance().registry(id, shader);
        return id;
    }
}
