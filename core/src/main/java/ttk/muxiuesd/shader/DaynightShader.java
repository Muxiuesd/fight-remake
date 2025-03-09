package ttk.muxiuesd.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.util.Log;

/**
 * 着色器实现的日夜循环效果
 * */
public class DaynightShader {
    private final ShaderProgram shader;
    private float timeCycle; // 0~1 循环

    public DaynightShader() {
        String vert = Gdx.files.internal("shaders/daynight/daynight.vert").readString();
        String frag = Gdx.files.internal("shaders/daynight/daynight.frag").readString();
        this.shader = new ShaderProgram(vert, frag);

        if (!this.shader.isCompiled()) {
            Log.error("日夜着色器：", this.shader.getLog());
        }
    }

    public void update(float delta) {
        // 每120秒完成一次昼夜循环
        timeCycle += delta / 120.0f;
        timeCycle %= 1.0f;
    }

    public void begin(Batch batch, OrthographicCamera camera) {
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformf("u_time", timeCycle);

        batch.setShader(shader);
    }

    public void end(Batch batch) {
        batch.setShader(null);
    }
}
