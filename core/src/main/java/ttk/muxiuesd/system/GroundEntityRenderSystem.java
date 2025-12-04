package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.render.IWorldGroundEntityRender;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.system.abs.EntityRenderSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 地面实体的渲染系统
 * */
public class GroundEntityRenderSystem extends EntityRenderSystem implements IWorldGroundEntityRender {
    public static final int RENDER_PRIORITY = 100;

    public GroundEntityRenderSystem (World world) {
        super(world);
    }


    @Override
    public void batchRender (Batch batch) {
        this.draw(batch);
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public void draw(Batch batch) {
        drawEntities(batch, this.getGroundEntities());
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        renderShapeEntities(batch, this.getGroundEntities());
    }

    @Override
    public int getRenderPriority () {
        return RENDER_PRIORITY;
    }

    public Array<Entity<?>> getGroundEntities() {
        return this.getCurEntitySystem().getRenderableEntities().get(RenderLayers.ENTITY_GROUND);
    }
}
