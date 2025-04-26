package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.CurveDrawer;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 钓鱼竿
 * <p>
 * 能抛出的钓鱼钩
 * */
public class ItemFishPole extends Item {

    public TextureRegion castTexture;
    //public LivingEntity owner;
    public EntityFishingHook hook;

    public boolean isCasting = false; //是否抛竿

    public ItemFishPole () {
        super(Type.COMMON, new Property().setMaxCount(1),
            Fight.getId("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png"));
        this.castTexture = getTextureRegion(Fight.getId("fish_pole_cast"), Fight.ItemTexturePath("fish_pole_cast.png"));
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        if (!this.isCasting) {
            EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");

            EntityFishingHook fishingHook = (EntityFishingHook)Gets.ENTITY(Fight.getId("fishing_hook"), es);
            fishingHook.setPosition(user.getPosition());
            fishingHook.setOwner(user);

            //TODO 鱼钩运动一段距离后停止

            //this.owner = user;
            this.hook = fishingHook;
            this.isCasting = true;
        }else {
            this.hook.removeSelf();
            this.hook = null;
            this.isCasting = false;
            //TODO 收起鱼钩
        }
        return super.use(world, user);
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity holder) {
        if (!this.isCasting) {
            if (texture == null) return;
            //没抛竿渲染
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    - holder.scaleX, holder.scaleY, rotation + 180);
            } else {
                batch.draw(texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    holder.scaleX, holder.scaleY, rotation);
            }
        }else {
            if (this.castTexture == null) return;
            //抛竿渲染
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(this.castTexture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    - holder.scaleX, holder.scaleY, rotation + 225f);
            } else {
                batch.draw(this.castTexture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    holder.scaleX, holder.scaleY, rotation - 45f);
            }
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (!this.isCasting) return;

        Direction direction = Util.getDirection();
        float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());

        //绘制鱼线
        LivingEntity hookOwner = this.hook.getOwner();
        Vector2 ownerPos = hookOwner.getCenter();
        float xOffset = hookOwner.getWidth() * 1.314f * MathUtils.cosDeg(rotation);
        float yOffset = hookOwner.getHeight()* 1.314f * MathUtils.sinDeg(rotation);
        ownerPos.add(xOffset, yOffset);

        Vector2 hookPos = this.hook.getCenter();
        hookPos.add(0, this.hook.getHeight() / 2 - 0.05f);

        if (ownerPos.x <= hookPos.x) CurveDrawer.drawCurve(batch, ownerPos, hookPos, -0.5f);
        else CurveDrawer.drawCurve(batch, hookPos, ownerPos, -0.5f);
    }
}
