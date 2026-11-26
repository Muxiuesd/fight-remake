package ttk.muxiuesd.render.world.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.block.instance.BlockFurnace;

/**
 * 熔炉的渲染器
 * <p>
 * 持有熔炉的默认贴图与燃烧贴图
 * */
public class FurnaceRenderer extends BlockRenderer.StandardRenderer<BlockFurnace> {
    private final TextureRegion workingTexture; //燃烧时的贴图

    public FurnaceRenderer () {
        super(Fight.ID("furnace"), Fight.BlockTexturePath("furnace.png"));
        this.workingTexture = Util.loadTextureRegion(
            Fight.ID("furnace_on"),
            Fight.BlockTexturePath("furnace_on.png")
        );
    }

    public TextureRegion getWorkingTexture () {
        return this.workingTexture;
    }
}
