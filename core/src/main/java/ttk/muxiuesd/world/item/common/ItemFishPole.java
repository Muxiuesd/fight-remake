package ttk.muxiuesd.world.item.common;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.ParticleSystem;
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
import ttk.muxiuesd.world.loottable.FishingLootTable;

/**
 * 钓鱼竿
 * <p>
 * 能抛出的钓鱼钩
 * */
public class ItemFishPole extends Item {
    public TextureRegion castTexture;
    public float castSpeed = 10f;
    public float pullSpeed = castSpeed * 2;

    public ItemFishPole () {
        super(Type.COMMON, new Property().setMaxCount(1)
                .add(PropertyTypes.ITEM_WITH_ENTITY, null)
                .add(PropertyTypes.FISHING_POLE_USING, false),
            Fight.ID("fish_pole"),
            Fight.ItemTexturePath("fish_pole.png"));

        this.castTexture = Util.loadTextureRegion(
            Fight.ID("fish_pole_cast"),
            Fight.ItemTexturePath("fish_pole_cast.png")
        );
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);

        if (!this.onUsing(itemStack)) {//抛出鱼钩
            //获取鱼钩
            EntityFishingHook fishingHook = Entities.FISHING_HOOK.create(world);
            fishingHook.setPosition(user.getPosition());
            fishingHook.setOnGround(false);
            fishingHook.setOwner(user)
                .setPole(itemStack)
                .setThrowDirection(Util.getDirection())  //未考虑其他LivingEntity抛竿的方向情况
                .setChunkSystem(world.getSystem(ChunkSystem.class))
                .setParticleSystem(world.getSystem(ParticleSystem.class));
            EntitySystem es = world.getSystem(EntitySystem.class);
            es.add(fishingHook);
            fishingHook.setEntitySystem(es);
            this.throwHook(itemStack, world, fishingHook);
            return super.use(itemStack, world, user);
        }else if (!hook.onCasting() && !hook.isReturning){ //鱼钩实体不在抛竿或者收杆途中则可以收起鱼钩
            Vector2 hookPos = hook.getCenterPos();
            ChunkSystem cs = world.getSystem(ChunkSystem.class);
            Block block = cs.getBlock(hookPos.x, hookPos.y);
            if (block instanceof BlockWater) {
                //需要鱼钩在水中才能钓到鱼
                //生成钓鱼战利品
                FishingLootTable.fastGenerate(100, genItemStack -> {
                    //ItemEntity itemEntity = (ItemEntity)Gets.ENTITY(Fight.getId("item_entity"), hook.getEntitySystem());
                    ItemEntity itemEntity = Pools.ITEM_ENTITY.obtain();
                    itemEntity.setEntitySystem(user.getEntitySystem());
                    itemEntity.setPosition(hookPos);
                    itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
                    itemEntity.setItemStack(genItemStack);
                    itemEntity.setSpeed(this.pullSpeed);
                    itemEntity.setCurSpeed(this.pullSpeed);
                    itemEntity.setVelocity(new Direction(hook.getCenterPos(), hook.getOwner().getCenterPos()).toVector2());
                    itemEntity.setOnGround(false);
                    itemEntity.setOnAirTimer(new TaskTimer(0.3f, 0, () -> {
                        itemEntity.setOnAirTimer(null);
                    }));

                    user.getEntitySystem().add(itemEntity);
                });
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
        if (this.onUsing(itemStack)) {
            this.removeHook(itemStack);
        }
    }

    @Override
    public void beDropped (ItemStack itemStack, World world, LivingEntity<?> dropper) {
        //钓鱼的时候被丢出来，则直接让鱼钩消失
        if (this.onUsing(itemStack)) {
            this.removeHook(itemStack);
        }
    }

    @Override
    public void update (float delta, ItemStack itemStack) {
        if (!this.onUsing(itemStack)) return;

        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        if (Util.getDistance(hook, hook.getOwner()) > 16f) {
            //鱼钩与使用者距离太远直接消失
            this.removeHook(itemStack);
        }
    }

    private void removeHook (ItemStack itemStack) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        hook.removeSelf();
        itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, null);
        itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, false);
        itemStack.getProperty().add(PropertyTypes.FISHING_POLE_USING, false);
    }

    /**
     * 抛出鱼钩
     * */
    public void throwHook (ItemStack itemStack, World world, EntityFishingHook fishingHook) {
        LivingEntity<?> owner = fishingHook.getOwner();
        if (owner instanceof Player player) {
            //玩家抛竿
            Vector2 ownerPos = player.getCenterPos();
            Vector2 mwp = Util.getMouseWorldPosition();
            float distance = Util.getDistance(ownerPos.x, ownerPos.y, mwp.x, mwp.y);
            fishingHook.setSpeed(Math.min(distance, this.castSpeed));
        }else {
            //TODO 其他生物抛竿的抛竿方向，速度方向
            fishingHook.setSpeed(this.castSpeed);
        }

        //添加鱼钩实体
        itemStack.getProperty().add(PropertyTypes.ITEM_WITH_ENTITY, fishingHook);
        itemStack.getProperty().add(PropertyTypes.ITEM_ON_USING, true);
        itemStack.getProperty().add(PropertyTypes.FISHING_POLE_USING, true);
    }

    /**
     * 收起鱼钩
     * */
    public void pullHook (ItemStack itemStack) {
        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        hook.isReturning = true;
        hook.setOnGround(false);
        hook.setSpeed(this.pullSpeed);
        itemStack.getProperty().add(PropertyTypes.FISHING_POLE_USING, false);
    }

    /**
     * 检查鱼竿物品是否正在使用
     * */
    public boolean onUsing (ItemStack itemStack) {
        return itemStack.getProperty().get(PropertyTypes.FISHING_POLE_USING);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.COMMON;
    }
}
