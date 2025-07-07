package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.system.DaynightSystem;
import ttk.muxiuesd.world.World;

/**
 * 世界实体渲染抽象类
 * */
public abstract class WorldEntityRenderProcessor extends WorldRenderProcessor {
    public WorldEntityRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public void handleRender (Batch batch, ShapeRenderer shapeRenderer) {
        batch.setProjectionMatrix(getCamera().combined);
        beginShader(batch);

        getRenderTasks().forEach(task -> task.render(batch, shapeRenderer));

        endShader();
    }

    @Override
    protected void beginShader (Batch batch) {
        //这里开始日夜着色
        DaynightSystem daynightSystem = (DaynightSystem) getWorld().getSystemManager().getSystem("DaynightSystem");
        daynightSystem.begin(batch);
    }

    @Override
    protected void endShader () {
        //这里结束日夜着色
        DaynightSystem daynightSystem = (DaynightSystem) getWorld().getSystemManager().getSystem("DaynightSystem");
        daynightSystem.end();
    }

}
