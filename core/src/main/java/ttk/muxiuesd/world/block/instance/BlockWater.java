package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.block.Block;

public class BlockWater extends Block {
    public BlockWater() {
        super(new Property().setFriction(0.2f), "block/water_overlay.png");
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/water_overlay.png", Texture.class));
    }

    @Override
    public void draw(Batch batch) {
        batch.setColor(new Color(0f, 0f, 0.8f, 0.6f));
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }
}
