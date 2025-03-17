package ttk.muxiuesd.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.system.DaynightSystem;
import ttk.muxiuesd.util.Log;

/**
 * 着色器实现的日夜循环效果
 * */
public class DaynightShader {
    private final ShaderProgram shader;
    private DaynightSystem daynightSystem;

    public DaynightShader(DaynightSystem daynightSystem) {
        this.daynightSystem = daynightSystem;
        String vert = Gdx.files.internal("shaders/daynight/daynight.vert").readString();
        String frag = Gdx.files.internal("shaders/daynight/daynight.frag").readString();
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
