package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 草方块
 * */
public class BlockGrass extends Block {
    public BlockGrass() {
        super(createProperty().setFriction(1f).setSounds(Sounds.GRASS),
            Fight.ID("grass"),
            Fight.BlockTexturePath("grass.png"));
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        batch.setColor(new Color(0f, 0.7f, 0.2f, 1f));
        super.draw(batch, x, y);
        batch.setColor(Color.WHITE);
    }
}
