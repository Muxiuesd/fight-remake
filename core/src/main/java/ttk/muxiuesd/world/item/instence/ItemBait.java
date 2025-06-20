package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 鱼饵
 * <p>
 * 使用后对鱼产生吸引效果
 * */
public class ItemBait extends Item {
    public ItemBait () {
        super(Type.CONSUMPTION, new Property().setMaxCount(64),
            Fight.getId("bait"),
            Fight.ItemTexturePath("bait.png"));
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity holder, ItemStack itemStack) {
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
