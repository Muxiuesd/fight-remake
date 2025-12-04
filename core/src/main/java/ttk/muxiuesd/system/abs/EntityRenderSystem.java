package ttk.muxiuesd.system.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体渲染抽象类
 * */
public abstract class EntityRenderSystem extends WorldSystem {
    private EntitySystem curEntitySystem;

    public EntityRenderSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.curEntitySystem = getManager().getSystem(EntitySystem.class);
    }

    public void drawEntities (Batch batch, Array<Entity<?>> entities) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : entities) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            //直接使用实体的参数渲染
            EntityRenderer.Context context = renderer.getContext(entity);
            renderer.draw(batch, entity, context);
            renderer.freeContext(context);
        }
    }

    public void renderShapeEntities (ShapeRenderer batch, Array<Entity<?>> entities) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : entities) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            EntityRenderer.Context context = renderer.getContext(entity);
            //直接使用实体的参数渲染
            renderer.drawShape(batch, entity, context);
            renderer.freeContext(context);
        }
    }



    public EntitySystem getCurEntitySystem () {
        return this.curEntitySystem;
    }

    public EntityRenderSystem setCurEntitySystem (EntitySystem curEntitySystem) {
        this.curEntitySystem = curEntitySystem;
        return this;
    }

    public Player getPlayer () {
        PlayerSystem playerSystem = this.getManager().getSystem(PlayerSystem.class);
        return playerSystem.getPlayer();
    }
}
