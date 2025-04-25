package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 钓鱼竿
 * <p>
 * 能抛出的钓鱼钩
 * */
public class ItemFishPole extends Item {
    public ItemFishPole () {
        super(Type.COMMON, new Property().setMaxCount(1),
            Fight.getId("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png"));
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        return super.use(world, user);
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity holder) {
        if (texture != null) {
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    -holder.scaleX, holder.scaleY, rotation + 180);
            }else {
                batch.draw(texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    holder.scaleX, holder.scaleY, rotation);
            }
        }
    }
}
