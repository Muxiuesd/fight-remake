package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.Block;

public class BlockGrass extends Block {
    public BlockGrass() {
        super(new ttk.muxiuesd.world.block.Block.Property()
            .setFriction(1f).setWalkSoundId(Fight.getId("grass_walk")),
            Fight.getId("grass"),
            Fight.getBlockTexture("grass.png"));
            //Fight.getBlockTexture("grass_2.jpg"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/grass.png", Texture.class));
    }

    @Override
    public void draw(Batch batch) {
        batch.setColor(new Color(0f, 0.7f, 0.2f, 1f));
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }
}
