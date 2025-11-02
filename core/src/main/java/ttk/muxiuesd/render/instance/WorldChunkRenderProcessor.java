package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.render.abs.WorldRenderProcessor;
import ttk.muxiuesd.world.World;

public class WorldChunkRenderProcessor extends WorldRenderProcessor {
    public WorldChunkRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
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

    @Override
    public boolean recognize (IRenderTask task) {
        if (task instanceof IWorldChunkRender) {
            addRenderTask(task);
            return true;
        }
        return false;
    }
}
