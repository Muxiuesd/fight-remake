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
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.player.Player;

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

    /**
     * 渲染顺序固定：先本层所有可见实体的影子，再本层所有实体本身
     * <p>
     * 声明为 final 是为了让"先影子后身体"这个顺序无法被子类改掉
     * */
    @Override
    public final void draw (Batch batch) {
        Array<Entity<?>> entities = this.getRenderEntities();
        this.drawEntityShadows(batch, entities);
        this.drawEntities(batch, entities);
    }

    /**
     * 形状渲染同样按本层实体列表走
     * */
    @Override
    public void renderShape (ShapeRenderer batch) {
        this.renderShapeEntities(batch, this.getRenderEntities());
    }

    /**
     * 本系统负责渲染的实体列表，由子类按渲染层提供
     * */
    protected abstract Array<Entity<?>> getRenderEntities ();

    /**
     * 影子趟：只画影子，不画身体
     * <p>
     * 没有影子的实体直接跳过，连渲染上下文都不取
     * */
    public void drawEntityShadows (Batch batch, Array<Entity<?>> entities) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : entities) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            //渲染器未注册（返回null）或者这个实体没有影子时不绘制
            if (renderer == null || !renderer.hasShadow()) continue;

            EntityRenderer.Context context = renderer.getContext(entity);
            try {
                renderer.drawShadow(batch, entity, context);
            } finally {
                renderer.freeContext(context);
            }
        }
    }

    public void drawEntities (Batch batch, Array<Entity<?>> entities) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : entities) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            //渲染器未注册（返回null）时不绘制该实体，避免崩溃
            if (renderer == null) continue;
            //直接使用实体的参数渲染
            EntityRenderer.Context context = renderer.getContext(entity);
            try {
                renderer.draw(batch, entity, context);
            } finally {
                renderer.freeContext(context);
            }
        }
    }

    public void renderShapeEntities (ShapeRenderer batch, Array<Entity<?>> entities) {
        Player player = getPlayer();
        if (player == null) return;

        for (Entity<?> entity : entities) {
            if (Util.getDistance(entity, player) > Fight.ENTITY_RENDER_RANGE.getValue()) continue;
            //获取实体的渲染器进行渲染
            EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(entity.getID());
            //渲染器未注册（返回null）时不绘制该实体，避免崩溃
            if (renderer == null) continue;
            EntityRenderer.Context context = renderer.getContext(entity);
            try {
                //直接使用实体的参数渲染
                renderer.drawShape(batch, entity, context);
            } finally {
                renderer.freeContext(context);
            }
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
