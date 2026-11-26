package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.Bullet;

/**
 * 子弹的渲染器
 * <p>
 * 子弹的贴图由创建时动态指定（实体只持贴图id数据，不持贴图资源），
 * 渲染器按贴图id懒加载并缓存贴图
 * */
public class BulletRenderer<T extends Bullet<T>> implements EntityRenderer<T> {
    private final ObjectMap<String, TextureRegion> textureRegionCache = new ObjectMap<>();

    @Override
    public void draw (Batch batch, T entity, Context context) {
        String textureId = entity.getTextureId();
        if (textureId == null) return;

        TextureRegion region = this.textureRegionCache.get(textureId);
        if (region == null) {
            //id→路径 映射在子弹构造时已注册，这里按id直接加载
            region = Util.loadTextureRegion(textureId, null);
            this.textureRegionCache.put(textureId, region);
        }
        if (region == null) return;

        batch.draw(region,
            context.x - context.width / 2, context.y - context.height / 2,
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY,
            context.rotation);
    }

    @Override
    public void drawShape (ShapeRenderer batch, T entity, Context context) {
    }
}
