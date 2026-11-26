package ttk.muxiuesd.interfaces.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 墙体的渲染器
 * <p>
 * 墙体同样是一个方块，贴图与渲染上下文直接复用{@link BlockRenderer}的那一套，
 * 这里只负责把类型限定成墙体 —— 取贴图的入口与方块完全一致
 * */
public interface WallRenderer<T extends Wall<?>> extends BlockRenderer<T> {

    /**
     * 普通标准的墙体渲染器
     * <p>
     * 持有墙体的贴图资源
     * */
    class StandardRenderer<T extends Wall<?>> implements WallRenderer<T> {
        private final Resource<TextureRegion> textureRegionResource;

        /**
         * @param textureId 贴图资源的id（一般与墙体id相同）
         * @param texturePath 贴图文件的路径，为null时通过id从已注册的映射中获取
         * */
        public StandardRenderer (String textureId, String texturePath) {
            this.textureRegionResource = Resource.ofTextureRegion(textureId, texturePath);
        }

        @Override
        public TextureRegion getTextureRegion () {
            return this.textureRegionResource.get();
        }

        @Override
        public void render (Batch batch, T wall, Context context) {
            TextureRegion textureRegion = getTextureRegion();
            if (textureRegion == null) return;
            batch.draw(textureRegion,
                context.x + BlockRenderer.StandardRenderer.OFFSET_X,
                context.y + BlockRenderer.StandardRenderer.OFFSET_Y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }
    }
}
