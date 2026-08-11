package ttk.muxiuesd.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.util.CurveDrawer;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.common.ItemFishPole;

/**
 * 钓竿的渲染器
 * <p>
 * 持有钓竿的默认贴图与抛竿贴图
 * */
public class FishPoleRenderer implements ItemRenderer<ItemFishPole> {
    private final Resource<TextureRegion> textureRegionResource;    //默认贴图资源
    private final TextureRegion castTexture;                        //抛竿贴图

    public FishPoleRenderer () {
        this.textureRegionResource = Resource.ofTextureRegion(
            Fight.ID("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png")
        );
        this.castTexture = Util.loadTextureRegion(
            Fight.ID("fish_pole_cast"),
            Fight.ItemTexturePath("fish_pole_cast.png")
        );
    }

    @Override
    public TextureRegion getTextureRegion () {
        return this.textureRegionResource.get();
    }

    @Override
    public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
        ItemFishPole fishPole = (ItemFishPole) itemStack.getItem();

        Vector2 renderStartPos = new Vector2(context.x - context.width / 2f, context.y - context.height / 2f);
        if (! fishPole.onUsing(itemStack)) {
            //没抛竿渲染
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getY(), direction.getX());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(getTextureRegion(),
                    renderStartPos.x , renderStartPos.y ,
                    context.originX, context.originY,
                    context.width, context.height,
                    - context.scaleX, context.scaleY,
                    rotation + 180);
            } else {
                batch.draw(getTextureRegion(),
                    renderStartPos.x , renderStartPos.y ,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    rotation);
            }
        }else {
            //抛竿渲染
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getY(), direction.getX());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(this.castTexture,
                    renderStartPos.x , renderStartPos.y ,
                    context.originX, context.originY,
                    context.width, context.height,
                    - context.scaleX, context.scaleY,
                    rotation + 225f);
            } else {
                batch.draw(this.castTexture,
                    renderStartPos.x , renderStartPos.y ,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    rotation - 45f);
            }
        }
    }

    @Override
    public void drawOnItemEntity (Batch batch, Context context, ItemEntity itemEntity) {
        batch.draw(getTextureRegion(),
            context.x, context.y + itemEntity.getPositionOffset().y,
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY,
            context.rotation
        );
    }

    /**
     * 钓鱼线的形状绘制
     * */
    @Override
    public void renderShapeOnHand (ShapeRenderer batch, LivingEntity<?> holder, ItemStack itemStack) {
        ItemFishPole fishPole = (ItemFishPole) itemStack.getItem();
        if (!fishPole.onUsing(itemStack)) return;

        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        Direction direction = Util.getDirection();
        float rotation = MathUtils.atan2Deg360(direction.getY(), direction.getX());
        //绘制鱼线
        LivingEntity<?> hookOwner = hook.getOwner();
        Vector2 ownerPos = hookOwner.getCenterPos();
        float xOffset = hookOwner.getWidth() * 1.314f * MathUtils.cosDeg(rotation);
        float yOffset = hookOwner.getHeight()* 1.314f * MathUtils.sinDeg(rotation);
        ownerPos.add(xOffset, yOffset);

        Vector2 hookPos = hook.getCenterPos();
        //让鱼线绘制在钩子上方
        hookPos.add(0, hook.getHeight() / 2f - 0.07f + hook.getPositionOffset().y);
        //控制鱼线绘制方向
        if (ownerPos.x <= hookPos.x) CurveDrawer.drawCurve(batch, ownerPos, hookPos, -0.5f);
        else CurveDrawer.drawCurve(batch, hookPos, ownerPos, -0.5f);
    }

    @Override
    public void renderShapeOnItemEntity (ShapeRenderer batch, ItemEntity itemEntity) {

    }
}
