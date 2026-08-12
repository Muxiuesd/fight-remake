package ttk.muxiuesd.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 植物渲染器
 * <p>
 * 持有植物不同生长等级的贴图，根据生长等级选择对应的贴图渲染
 * */
public class BotanyRenderer<T extends Botany> implements BlockRenderer<T> {
    private final TextureRegion[] textureRegions;   //不同生长等级的贴图

    /**
     * @param textureNames 不同生长等级的贴图文件名（位于 botany/crops/ 目录下）
     * */
    public BotanyRenderer (String... textureNames) {
        this.textureRegions = new TextureRegion[textureNames.length];
        for (int i = 0; i < textureNames.length; i++) {
            this.textureRegions[i] = Util.loadTextureRegion(
                Fight.ID(textureNames[i]),
                Fight.BotanyTexturePath("crops/" + textureNames[i])
            );
        }
    }

    @Override
    public void render (Batch batch, T botany, Context context) {
        int level = botany.getGrowLevel();
        //如果生长等级超过对应的贴图，就使用最大的贴图
        TextureRegion textureRegion = this.textureRegions[Math.min(level, this.textureRegions.length - 1)];
        if (textureRegion == null) return;

        batch.draw(
            textureRegion,
            context.x + BlockRenderer.StandardRenderer.OFFSET_X, context.y,
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY,
            context.rotation
        );
    }
}
