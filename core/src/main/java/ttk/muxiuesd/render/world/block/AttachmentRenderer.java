package ttk.muxiuesd.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 附着物渲染器
 * <p>
 * 持有多个贴图，根据生长等级选择对应的贴图渲染（不是植物就把生长等级当成0，只用第一张贴图）
 * */
public class AttachmentRenderer<T extends Attachment> implements BlockRenderer<T> {
    private final TextureRegion[] textureRegions;   //不同生长等级的贴图

    /**
     * @param textureNames 不同生长等级的贴图文件名（位于 blocks/attachment/crops/ 目录下）
     * */
    public AttachmentRenderer (String... textureNames) {
        this.textureRegions = new TextureRegion[textureNames.length];
        for (int i = 0; i < textureNames.length; i++) {
            this.textureRegions[i] = Util.loadTextureRegion(
                Fight.ID(textureNames[i]),
                Fight.AttachmentTexturePath("crops/" + textureNames[i])
            );
        }
    }

    @Override
    public TextureRegion getTextureRegion () {
        //没有生长等级语义的调用方（如取固定贴图）就用第一帧
        return this.textureRegions[0];
    }

    @Override
    public TextureRegion getTextureRegion (Block block) {
        //只有植物才有生长等级，其他的附着物都按0级取图
        int level = block instanceof Botany botany ? botany.getGrowLevel() : 0;
        return this.textureRegions[Math.min(level, this.textureRegions.length - 1)];
    }

    @Override
    public void render (Batch batch, T attachment, Context context) {
        TextureRegion textureRegion = this.getTextureRegion(attachment);
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
