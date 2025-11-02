package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 鱼饵
 * <p>
 * 使用后对鱼产生吸引效果
 * */
public class ItemBait extends ConsumptionItem {
    public ItemBait () {
        super(new Property().setMaxCount(64),
            Fight.ID("bait"),
            Fight.ItemTexturePath("bait.png"));
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity<?> holder, ItemStack itemStack) {
        if (texture != null) {
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            batch.draw(texture, holder.x, holder.y,
                holder.width / 2, holder.height / 2,
                holder.width, holder.height,
                -holder.scaleX, -holder.scaleY, rotation);
        }
    }
}
