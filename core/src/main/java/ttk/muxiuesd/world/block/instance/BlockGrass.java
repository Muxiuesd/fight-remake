package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.Block;

public class BlockGrass extends Block {
    public BlockGrass() {
        super(new ttk.muxiuesd.world.block.Block.Property()
            .setFriction(1f).setWalkSoundId(Fight.getId("grass_walk")),
            Fight.getId("grass"),
            Fight.getBlockTexture("grass_2.jpg"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/grass.png", Texture.class));
    }

    /*@Override
    public void draw(Batch batch) {
        //batch.setColor(new Color(0f, 0.7f, 0.1f, 1f));
        *//*ShaderProgram previousShader = batch.getShader();

        ShaderProgram grassShader = GrassShader.getInstance().getShader();
        // 渲染时设置参数
        grassShader.bind();
        grassShader.setUniformf("u_grassColor", 0.2f, 0.8f, 0.3f); // 草绿色
        grassShader.setUniformf("u_brightness", 1.2f);
        batch.setShader(grassShader);
        super.draw(batch);
        batch.setShader(null);

        batch.setShader(previousShader);*//*
        //batch.setColor(Color.WHITE);
        Color perColor = batch.getColor();
        batch.setColor(new Color(0f, 0.7f, 0.1f, 1f));
        super.draw(batch);
        batch.setColor(perColor);
    }*/
}
