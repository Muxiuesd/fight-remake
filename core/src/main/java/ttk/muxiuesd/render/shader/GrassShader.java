package ttk.muxiuesd.render.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import game.muxiuesd.bedrockcore.util.FileUtil;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;

/**
 * 草地的着色器
 * TODO 暂时没用到
 * */
public class GrassShader {
    private ShaderProgram shader;

    private static GrassShader grassShader;

    public GrassShader() {
        String vert = FileUtil.readText(UnifiedFileUtil.internalFile("shaders/grass/grass.vert"));
        String frag = FileUtil.readText(UnifiedFileUtil.internalFile("shaders/grass/grass.frag"));
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
