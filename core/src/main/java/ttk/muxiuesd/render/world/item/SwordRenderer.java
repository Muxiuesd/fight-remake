package ttk.muxiuesd.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 剑类物品的渲染器
 * */
public class SwordRenderer extends ItemRenderer.StandardRenderer<Item> {
    public SwordRenderer (String name) {
        super(Fight.ID(name), Fight.ItemTexturePath(name + ".png"));
    }
    /**
     * @param textureId   贴图资源的id（一般与物品id相同，方块物品为方块的id）
     * @param texturePath 贴图文件的路径，为null时通过id从已注册的映射中获取
     */
    public SwordRenderer (String textureId, String texturePath) {
        super(textureId, texturePath);
    }

    @Override
    public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
        super.drawOnHand(batch, context, holder, itemStack);
    }
}
