package ttk.muxiuesd.render.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.Fight;

/**
 * 着色器注册
 * */
public class ShadersReg {

    // 符合 GLSL 330 core 语法的顶点着色器
    private static final String VERTEX_SHADER_330 =
        "#version 330 core\n" +
            "layout(location = 0) in vec2 a_position;\n" +
            "layout(location = 1) in vec4 a_color;\n" +
            "layout(location = 2) in vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "out vec4 v_color;\n" +
            "out vec2 v_texCoords;\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);\n" +
            "}";

    // 符合 GLSL 330 core 语法的片段着色器
    private static final String FRAGMENT_SHADER_330 =
        "#version 330 core\n" +
            "in vec4 v_color;\n" +
            "in vec2 v_texCoords;\n" +
            "out vec4 f_color;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "    f_color = v_color * texture(u_texture, v_texCoords);\n" +
            "}";


    public static final ShaderProgram DefaultShader = new ShaderProgram(
        VERTEX_SHADER_330,
        FRAGMENT_SHADER_330
    );

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
        ShaderScheduler.getInstance().registry(id, SpriteBatch.createDefaultShader());
        return id;
    }
}
