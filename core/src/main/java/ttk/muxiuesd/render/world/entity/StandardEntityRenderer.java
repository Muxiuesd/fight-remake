package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 最标准基础实体的渲染器
 * */
public class StandardEntityRenderer<T extends Entity<?>> implements EntityRenderer<T> {
    @Override
    public void draw (Batch batch, T entity, Context context) {
        //最基础的绘制，绘制实体的身体贴图
        TextureRegion bodyTextureRegion = entity.getBodyTextureRegion();
        if (bodyTextureRegion != null) {
            batch.draw(bodyTextureRegion,
                context.x - context.width / 2, context.y - context.height / 2,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation
            );
        }
    }

    @Override
    public void drawShape (ShapeRenderer batch, T entity, Context context) {
    }
}
