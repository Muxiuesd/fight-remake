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
    /**
     * 水方块的渲染器，自持水的贴图
     * */
    public static final BlockRenderer<BlockWater> RENDERER = new BlockRenderer.StandardRenderer<>(
        Fight.ID("water"),
        Fight.BlockTexturePath("water_still.png")
    ) {
        @Override
        public void render (Batch batch, BlockWater block, Context context) {
            batch.setColor(new Color(0f, 0f, 0.8f, 1f));
            super.render(batch, block, context);
            batch.setColor(Color.WHITE);
        }
    };

    public BlockWater (Property property) {
        super(property);
    }
}
