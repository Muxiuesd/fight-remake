package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.system.LightSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.light.PointLight;

/**
 * 火把物品
 * */
public class ItemTorch extends Sword {
    private final Color color = new Color(0.88f, 0.7f, 0.0f, 0.3f);

    public ItemTorch () {
        super(createDefaultProperty()
                .add(PropertyTypes.WEAPON_ATTACK_RANGE, 2f)
                .add(PropertyTypes.WEAPON_DURATION, 50),
            Fight.getId("torch"),
            Fight.ItemTexturePath("torch.png"));
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity<?> holder, ItemStack itemStack) {
        super.drawOnHand(batch, holder, itemStack);
        //计算光源的正确位置
        Vector2 holderCenter = holder.getCenter();
        Direction direction = holder.getDirection();
        float deg = direction.angleDeg() + holder.getSwingHandDegreeOffset();
        float xOffset = holder.getWidth() * 1.014f * MathUtils.cosDeg(deg);
        float yOffset = holder.getHeight()* 1.014f * MathUtils.sinDeg(deg);

        World world = holder.getEntitySystem().getWorld();
        LightSystem lightSystem = (LightSystem) world.getSystemManager().getSystem("LightSystem");
        PointLight light = new PointLight(
            new Vector2(holderCenter).add(xOffset, yOffset),
            Color.WHITE,
            1f
        );
        lightSystem.useLight(light);
    }
}
