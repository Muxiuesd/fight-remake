package ttk.muxiuesd.render.shader;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import game.muxiuesd.bedrockcore.util.FileUtil;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;

/**
 * 着色器实现的日夜循环效果
 * */
public class DaynightShader {
    private final ShaderProgram shader;

    public DaynightShader() {
        String vert = FileUtil.readText(UnifiedFileUtil.internalFile("shaders/daynight/daynight.vert"));
        String frag = FileUtil.readText(UnifiedFileUtil.internalFile("shaders/daynight/daynight.frag"));
        this.shader = new ShaderProgram(vert, frag);

        if (!this.shader.isCompiled()) {
            Log.error("日夜着色器：", this.shader.getLog());
        }
    }

    public void begin(Batch batch, OrthographicCamera camera) {

    }

    public void end(Batch batch) {
        batch.setShader(null);
    }
}
