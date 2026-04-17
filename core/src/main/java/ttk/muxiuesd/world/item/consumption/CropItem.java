package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 农作物类型的物品
 * <p>
 * 对着耕地使用可以种植，或者可以直接食用
 * */
public class CropItem extends ConsumptionItem {
    private Botany botany;

    public CropItem (String name, Botany botany) {
        super(name);
        this.botany = botany;
    }

    public CropItem (Property property, String textureId) {
        super(property, textureId);
    }

    public CropItem (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //TODO 对着耕地使用可以种植
        Vector2 mouseWorldPosition = Util.getMouseWorldPosition();
        ChunkSystem chunkSystem = world.getSystem(ChunkSystem.class);
        Block block = chunkSystem.getBlock(mouseWorldPosition);
        //目前只能在耕地上种植农作物
        if (block == Blocks.FARMLAND_DRY) {
            chunkSystem.placeBotany(this.getBotany(), mouseWorldPosition.x, mouseWorldPosition.y);
        }
        return super.use(itemStack, world, user);
    }

    public Botany getBotany () {
        return this.botany;
    }

    public CropItem setBotany (Botany botany) {
        this.botany = botany;
        return this;
    }
}
