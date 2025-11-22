package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.IWorldGroundEntityRender;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.system.abs.EntityRenderSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
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
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : this.getGroundEntities()) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            //直接使用实体的参数渲染
            EntityRenderer.Context context = renderer.getContext(entity);
            renderer.draw(batch, entity, context);
            renderer.freeContext(context);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : this.getGroundEntities()) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            EntityRenderer.Context context = renderer.getContext(entity);
            //直接使用实体的参数渲染
            renderer.drawShape(batch, entity, context);
            renderer.freeContext(context);
        }
    }

    @Override
    public int getRenderPriority () {
        return RENDER_PRIORITY;
    }

    public Array<Entity<?>> getGroundEntities() {
        return this.getCurEntitySystem().getRenderableEntities().get(RenderLayers.ENTITY_GROUND);
    }
}
