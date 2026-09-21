package ttk.muxiuesd.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

public class LongSwordRenderer extends SwordRenderer {
    public LongSwordRenderer (String name) {
        super(name);
    }

    public LongSwordRenderer (String textureId, String texturePath) {
        super(textureId, texturePath);
    }

    @Override
    public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
        context.scaleX *= 2f;
        context.scaleY *= 2f;
        super.drawOnHand(batch, context, holder, itemStack);
    }
}
