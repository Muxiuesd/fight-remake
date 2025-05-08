package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.render.abs.WorldRenderProcessor;
import ttk.muxiuesd.system.DaynightSystem;
import ttk.muxiuesd.world.World;

public class WorldChunkRenderProcessor extends WorldRenderProcessor {
    public WorldChunkRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public void handleRender (Batch batch, ShapeRenderer shapeRenderer) {
        batch.setProjectionMatrix(getCamera().combined);
        beginShader(batch);

        //getWorld().draw(batch);
        //getWorld().renderShape(shapeRenderer);
        getRenderTasks().forEach(task -> task.render(batch, shapeRenderer));

        endShader();
    }

    @Override
    protected void beginShader (Batch batch) {
        //这里开始日夜着色
        DaynightSystem daynightSystem = (DaynightSystem) getWorld().getSystemManager().getSystem("DaynightSystem");
        daynightSystem.begin();
    }

    @Override
    protected void endShader () {
        //这里结束日夜着色
        DaynightSystem daynightSystem = (DaynightSystem) getWorld().getSystemManager().getSystem("DaynightSystem");
        daynightSystem.end();
    }
}
