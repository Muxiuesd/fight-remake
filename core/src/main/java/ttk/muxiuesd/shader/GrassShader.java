package ttk.muxiuesd.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.util.Log;

/**
 * 草地的着色器
 * TODO 暂时没用到
 * */
public class GrassShader {
    private ShaderProgram shader;

    private static GrassShader grassShader;

    public GrassShader() {
        String vert = Gdx.files.internal("shaders/grass/grass.vert").readString();
        String frag = Gdx.files.internal("shaders/grass/grass.frag").readString();
        this.shader = new ShaderProgram(vert, frag);

        if (!this.shader.isCompiled()) {
            Log.error("草地着色器：", this.shader.getLog());
        }
    }

    public static GrassShader getInstance() {
        if (grassShader == null) {
            grassShader = new GrassShader();
        }
        return grassShader;
    }

    public ShaderProgram getShader() {
        return shader;
    }
}
