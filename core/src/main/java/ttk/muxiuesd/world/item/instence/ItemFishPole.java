package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.util.CurveDrawer;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.TaskTimer;
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
import ttk.muxiuesd.world.loottable.FishingLootTable;

/**
 * 钓鱼竿
 * <p>
 * 能抛出的钓鱼钩
 * */
public class ItemFishPole extends Item {
    public TextureRegion castTexture;
    //public EntityFishingHook hook;
    //public boolean isCasting = false; //是否抛竿
    public float castSpeed = 10f;
    public float pullSpeed = castSpeed * 2;

    public ItemFishPole () {
        super(Type.COMMON, new Property().setMaxCount(1).add(PropertyTypes.ITEM_WITH_ENTITY, null),
            Fight.getId("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png"));
        this.castTexture = getTextureRegion(Fight.getId("fish_pole_cast"), Fight.ItemTexturePath("fish_pole_cast.png"));
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);

        if (!itemStack.onUsing()) {//抛出鱼钩
            EntitySystem es = user.getEntitySystem();
            //获取鱼钩
            EntityFishingHook fishingHook = (EntityFishingHook)Gets.ENTITY(Fight.getId("fishing_hook"), es);
            fishingHook.setPosition(user.getPosition());
            fishingHook.setOnGround(false);
            fishingHook.setOwner(user)
                .setPole(itemStack)
                .setThrowDirection(Util.getDirection())  //未考虑其他LivingEntity抛竿的方向情况
                .setChunkSystem((ChunkSystem) world.getSystemManager().getSystem("ChunkSystem"))
                .setParticleSystem((ParticleSystem) world.getSystemManager().getSystem("ParticleSystem"));

            this.throwHook(itemStack, world, fishingHook);
            return super.use(itemStack, world, user);
        }else if (!hook.onCasting() && !hook.isReturning){ //鱼钩实体不在抛竿或者收杆途中则可以收起鱼钩
            Vector2 hookPos = hook.getCenter();
            ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");
            Block block = cs.getBlock(hookPos.x, hookPos.y);
            if (block instanceof BlockWater) {
                //需要鱼钩在水中才能钓到鱼
                //生成钓鱼战利品
                FishingLootTable.fastGenerate(100, genItemStack -> {
                    ItemEntity itemEntity = (ItemEntity)Gets.ENTITY(Fight.getId("item_entity"), hook.getEntitySystem());
                    itemEntity.setPosition(hookPos);
                    itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);
                    itemEntity.setItemStack(genItemStack);
                    itemEntity.setSpeed(this.pullSpeed);
                    itemEntity.setCurSpeed(this.pullSpeed);
                    itemEntity.setVelocity(new Direction(hook.getCenter(), hook.getOwner().getCenter()));
                    itemEntity.setOnGround(false);
                    itemEntity.setOnAirTimer(new TaskTimer(0.3f, 0, () -> {
                        itemEntity.setOnAirTimer(null);
                    }));
                });
                //ItemStack lootStack1 = FishingLootTable.generate(Fight.getId("rubbish"));
            }

            this.pullHook(itemStack);
            return super.use(itemStack, world, user);
        }
        //到这里就说明在抛竿或者收杆的动作之中，所以是使用失败的
        return false;
    }

    @Override
    public void putDown (ItemStack itemStack, World world, LivingEntity<?> holder) {
        //放下钓鱼竿也马上让鱼钩消失
        if (itemStack.onUsing()) {
            EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
            hook.removeSelf();
            itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, null);
            itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, false);
        }
    }

    @Override
    public void beDropped (ItemStack itemStack, World world, LivingEntity<?> dropper) {
        //钓鱼的时候被丢出来，则直接让鱼钩消失
        if (itemStack.onUsing()) {
            //this.hook.removeSelf();
            //this.hook = null;
            EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
            hook.removeSelf();
            itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, null);
            //this.isCasting = false;
            itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, false);
        }
    }

    @Override
    public void update (float delta, ItemStack itemStack) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        if (itemStack.onUsing() && Util.getDistance(hook, hook.getOwner()) > 16f) {
            //鱼钩与使用者距离太远直接消失
            hook.removeSelf();
            itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, null);
            //this.isCasting = false;
            itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, false);
        }
    }

    /**
     * 抛出鱼钩
     * */
    public void throwHook (ItemStack itemStack, World world, EntityFishingHook fishingHook) {
        //Boolean onUsing = itemStack.getProperty().get(PropertyTypes.ITEM_ON_USING);

        LivingEntity<?> owner = fishingHook.getOwner();
        if (owner instanceof Player player) {
            //玩家抛竿
            Vector2 ownerPos = player.getCenter();
            Vector2 mwp = Util.getMouseWorldPosition();
            float distance = Util.getDistance(ownerPos.x, owner.y, mwp.x, mwp.y);
            fishingHook.setSpeed(Math.min(distance, this.castSpeed));
        }else {
            //TODO 其他生物抛竿的抛竿方向，速度方向
            fishingHook.setSpeed(this.castSpeed);
        }

        //this.hook = fishingHook;
        //添加鱼钩实体
        itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, fishingHook);
        //this.isCasting = true;
        itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, true);
    }

    /**
     * 收起鱼钩
     * */
    public void pullHook (ItemStack itemStack) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        hook.isReturning = true;
        hook.setOnGround(false);
        hook.setSpeed(this.pullSpeed);
    }

    @Override
    public void drawOnHand (Batch batch, LivingEntity<?> holder, ItemStack itemStack) {
        if (!itemStack.onUsing()) {
            if (this.texture == null) return;
            //没抛竿渲染
            Direction direction = holder.getDirection();
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
            Direction direction = holder.getDirection();
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
    public void drawOnWorld (Batch batch, ItemEntity itemEntity) {
        if (this.texture != null) {
            batch.draw(this.texture, itemEntity.x, itemEntity.y + itemEntity.getPositionOffset().y,
                itemEntity.originX, itemEntity.originY,
                itemEntity.width, itemEntity.height,
                itemEntity.scaleX, itemEntity.scaleY, itemEntity.rotation);
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch, ItemStack itemStack) {
        if (!itemStack.onUsing()) return;

        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        Direction direction = Util.getDirection();
        float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
        //绘制鱼线
        LivingEntity<?> hookOwner = hook.getOwner();
        Vector2 ownerPos = hookOwner.getCenter();
        float xOffset = hookOwner.getWidth() * 1.314f * MathUtils.cosDeg(rotation);
        float yOffset = hookOwner.getHeight()* 1.314f * MathUtils.sinDeg(rotation);
        ownerPos.add(xOffset, yOffset);

        Vector2 hookPos = hook.getCenter();
        //让鱼线绘制在钩子上方
        hookPos.add(0, hook.getHeight() / 2 - 0.07f + hook.getPositionOffset().y);
        //控制鱼线绘制方向
        if (ownerPos.x <= hookPos.x) CurveDrawer.drawCurve(batch, ownerPos, hookPos, -0.5f);
        else CurveDrawer.drawCurve(batch, hookPos, ownerPos, -0.5f);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.COMMON;
    }
}
