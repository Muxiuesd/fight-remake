package ttk.muxiuesd.render.world.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.system.LightSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.weapon.ItemTorch;
import ttk.muxiuesd.world.light.PointLight;

/**
 * 物品火把的渲染器
 * */
public class TorchRenderer extends ItemRenderer.StandardRenderer<ItemTorch> {
    @Override
    public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
        super.drawOnHand(batch, context, holder, itemStack);
        //计算光源的正确位置
        Vector2 holderCenter = holder.getCenter();
        Direction direction = holder.getDirection();
        float deg = direction.angleDeg() + holder.getSwingHandDegreeOffset();
        float xOffset = holder.getWidth() * 1.014f * MathUtils.cosDeg(deg);
        float yOffset = holder.getHeight()* 1.014f * MathUtils.sinDeg(deg);
        //获取光源系统并添加光源信息
        World world = holder.getEntitySystem().getWorld();
        LightSystem lightSystem = world.getSystem(LightSystem.class);
        PointLight light = new PointLight(
            new Vector2(holderCenter).add(xOffset, yOffset),
            Color.WHITE,
            1f
        );
        lightSystem.useLight(light);
    }
}
