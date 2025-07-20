package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.render.IWorldUndergroundEntityRender;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.system.abs.EntityRenderSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 地下实体渲染系统
 * */
public class UndergroundEntityRenderSystem extends EntityRenderSystem implements IWorldUndergroundEntityRender {
    public static final int RENDER_PRIORITY = 100;

    public UndergroundEntityRenderSystem (World world) {
        super(world);
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
        this.renderShape(shapeRenderer);
    }

    @Override
    public void draw(Batch batch) {
        for (Entity<?> entity : this.getUndergroundEntities()) {
            entity.draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        for (Entity<?> entity : this.getUndergroundEntities()) {
            entity.renderShape(batch);
        }
    }

    @Override
    public int getRenderPriority () {
        return RENDER_PRIORITY;
    }

    public Array<Entity<?>> getUndergroundEntities () {
        return this.getCurEntitySystem().getRenderableEntities().get(RenderLayers.ENTITY_UNDERGROUND);
    }
}
