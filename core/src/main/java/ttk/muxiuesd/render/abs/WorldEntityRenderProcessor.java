package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.world.World;

/**
 * 世界实体渲染抽象类
 * */
public abstract class WorldEntityRenderProcessor extends WorldRenderProcessor {
    public WorldEntityRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public ShaderProgram beginShader (Batch batch) {
        //这里开始日夜着色
        ShaderProgram shaderProgram = super.beginShader(batch);
        shaderProgram.setUniformMatrix("u_projTrans", getCamera().combined);

        return shaderProgram;
    }

    @Override
    public void endShader () {
        //这里结束日夜着色
        super.endShader();
    }
}
