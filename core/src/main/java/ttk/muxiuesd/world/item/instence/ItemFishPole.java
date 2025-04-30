package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.util.CurveDrawer;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 钓鱼竿
 * <p>
 * 能抛出的钓鱼钩
 * */
public class ItemFishPole extends Item {
    public TextureRegion castTexture;
    public EntityFishingHook hook;
    public boolean isCasting = false; //是否抛竿
    public float castSpeed = 10f;


    public ItemFishPole () {
        super(Type.COMMON, new Property().setMaxCount(1),
            Fight.getId("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png"));
        this.castTexture = getTextureRegion(Fight.getId("fish_pole_cast"), Fight.ItemTexturePath("fish_pole_cast.png"));
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        if (!this.isCasting) {//抛出鱼钩
            EntitySystem es = user.getEntitySystem();
            //获取鱼钩
            EntityFishingHook fishingHook = (EntityFishingHook)Gets.ENTITY(Fight.getId("fishing_hook"), es);
            fishingHook.setPosition(user.getPosition());
            fishingHook.setOwner(user)
                .setDirection(Util.getDirection())  //未考虑其他LivingEntity抛竿的方向情况
                .setChunkSystem((ChunkSystem) world.getSystemManager().getSystem("ChunkSystem"))
                .setParticleSystem((ParticleSystem) world.getSystemManager().getSystem("ParticleSystem"));

            this.throwHook(world, fishingHook);
        }else {//收起鱼钩
            Vector2 hookPos = this.hook.getCenter();
            ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");
            Block block = cs.getBlock(hookPos.x, hookPos.y);
            if (block instanceof BlockWater) {
                //需要鱼钩在水中才能钓到鱼
                ItemEntity itemEntity = (ItemEntity)Gets.ENTITY(Fight.getId("item_entity"), hook.getEntitySystem());
                itemEntity.setPosition(hookPos);
                itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);
                itemEntity.setItemStack(new ItemStack(Gets.ITEM(Fight.getId("fish")), 1));
            }

            //TODO 收起鱼钩的运动动画
            this.pullHook();
        }
        return super.use(world, user);
    }

    @Override
    public void update (float delta) {
        if (this.isCasting) {
            //鱼钩与使用者距离太远自动收回
            if (Util.getDistance(this.hook, this.hook.getOwner()) > 16f) {
                this.pullHook();
            }
        }
    }

    /**
     * 抛出鱼钩
     * */
    public void throwHook (World world, EntityFishingHook fishingHook) {
        LivingEntity owner = fishingHook.getOwner();
        if (owner instanceof Player player) {
            //玩家抛竿
            Vector2 ownerPos = player.getCenter();
            HandleInputSystem his = (HandleInputSystem) world.getSystemManager().getSystem("HandleInputSystem");
            Vector2 mwp = his.getMouseWorldPosition();
            float distance = Util.getDistance(ownerPos.x, owner.y, mwp.x, mwp.y);
            fishingHook.setSpeed(Math.min(distance, this.castSpeed));
        }else {
            //TODO 其他生物抛竿的抛竿方向，速度方向
            fishingHook.setSpeed(this.castSpeed);
        }

        this.hook = fishingHook;
        this.isCasting = true;
    }

    /**
     * 收起鱼钩
     * */
    public void pullHook () {
        this.hook.removeSelf();
        this.hook = null;
        this.isCasting = false;
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
        //让鱼线绘制在钩子上方
        hookPos.add(0, this.hook.getHeight() / 2 - 0.07f + this.hook.getPositionOffset().y);

        if (ownerPos.x <= hookPos.x) CurveDrawer.drawCurve(batch, ownerPos, hookPos, -0.5f);
        else CurveDrawer.drawCurve(batch, hookPos, ownerPos, -0.5f);
    }

}
