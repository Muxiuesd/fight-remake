package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

public class BlockWater extends Block {
    public BlockWater() {
        super(new Property().setFriction(0.2f),
            Fight.getId("water"),
            Fight.getBlockTexture("water_still.png"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/water_overlay.png", Texture.class));
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        batch.setColor(new Color(0f, 0f, 0.8f, 1f));
        super.draw(batch, x, y);
        batch.setColor(Color.WHITE);
    }
}
