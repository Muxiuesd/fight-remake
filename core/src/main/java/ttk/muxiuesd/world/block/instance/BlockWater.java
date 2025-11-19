package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 水方块
 * */
public class BlockWater extends Block {
    public static final BlockRenderer<BlockWater> RENDERER = (batch, block, context) -> {
        batch.setColor(new Color(0f, 0f, 0.8f, 1f));
        batch.draw(block.getTextureRegion(),
            context.x, context.y,
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY,
            context.rotation);
        batch.setColor(Color.WHITE);
    };

    public BlockWater() {
        super(createProperty().setFriction(0.26f),
            Fight.ID("water"),
            Fight.BlockTexturePath("water_still.png"));
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        batch.setColor(new Color(0f, 0f, 0.8f, 1f));
        super.draw(batch, x, y);
        batch.setColor(Color.WHITE);
    }
}
