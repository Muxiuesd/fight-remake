package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import game.muxiuesd.bedrockcore.app.interfaces.serialization.Codec;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.StatusEffects;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家
 */
public class Player extends LivingEntity<Player> {
    public static final int BACKPACK_SIZE = 36;
    //碰撞箱起点（前两个值）和终点（后两个值）的偏移
    public static final Vector4 HITBOX_OFFSET = new Vector4(0.1f, 0.1f, -0.1f, -0.1f);


    public TextureRegion shield;
    public TaskTimer defendCDTimer; //防御状态冷却计时器
    public TaskTimer defendDurationTimer; //防御状态持续计时器
    public boolean isDefend = false;
    public float defenseRadius = 1.23f; //防御半径

    public Player (World world, EntityType<? super Player> entityType) {
        this(world, entityType, 20, 20);
    }
    public Player(World world, EntityType<? super Player> entityType, float maxHealth, float curHealth) {
        super(world, entityType, maxHealth, curHealth, BACKPACK_SIZE);
        renderHandItem = true;
        speed = 3.3f;
        curSpeed = speed;
        textureRegion = getTextureRegion(Fight.ID("player"), "player/player.png");
        this.shield = getTextureRegion(Fight.ID("player_shield"), "player/shield.png");

        this.defendCDTimer = Pools.TASK_TIMER.obtain().setMaxSpan(2f).setCurSpan(0f)
            .setTask(() -> this.isDefend = true);
        this.defendDurationTimer = Pools.TASK_TIMER.obtain().setMaxSpan(0.3f).setCurSpan(0f)
            .setTask(() ->  {
                //到时间了就取消防御状态
                this.isDefend = false;
            });
        fastAddBodyHitBox();

        backpack.setItemStack(0, new ItemStack(Items.IRON_SWORD));
        backpack.setItemStack(1, new ItemStack(Items.TEST_WEAPON));
        backpack.setItemStack(2, new ItemStack(Items.STICK));
        backpack.setItemStack(3, new ItemStack(Items.FURNACE));
        backpack.setItemStack(4, new ItemStack(Items.CRAFTING_TABLE));
        backpack.setItemStack(5, new ItemStack(Items.FISH_POLE));
        backpack.setItemStack(6, new ItemStack(Items.SMOOTH_STONE));
        backpack.setItemStack(7, new ItemStack(Items.TORCH));
        backpack.setItemStack(8, new ItemStack(Items.IRON_SWORD));
        backpack.setItemStack(9, new ItemStack(Items.TEST_WEAPON));
        backpack.setItemStack(10, new ItemStack(Items.STICK));
        backpack.setItemStack(11, new ItemStack(Items.FURNACE));
        backpack.setItemStack(18, new ItemStack(Items.CRAFTING_TABLE));
        backpack.setItemStack(28, new ItemStack(Items.FISH_POLE));
        backpack.setItemStack(30, new ItemStack(Items.DIAMOND_HELMET));
        backpack.setItemStack(31, new ItemStack(Items.DIAMOND_CHESTPLATE));
        backpack.setItemStack(32, new ItemStack(Items.DIAMOND_LEGGINGS));
        backpack.setItemStack(33, new ItemStack(Items.DIAMOND_BOOTS));
        backpack.setItemStack(35, new ItemStack(Items.TORCH));

        setEffect(StatusEffects.HEALING, 500f, 2);
        setEffect(StatusEffects.POISON, 500f, 1);

        Log.print(this.getClass().getName(),"Player 初始化完成");
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (!this.isDefend) {
            //不在防御状态就计时
            this.defendCDTimer.update(delta);
        } else {
            //防御状态下
            if (this.defendDurationTimer.isReady()) {
            }else {
                //没到时间就继续计时
                this.defendDurationTimer.update(delta);
            }
        }

        //setCullingArea(x, y, width, height);
    }


    @Override
    public ItemEntity dropItem (int index, int amount) {
        ItemEntity itemEntity = super.dropItem(index, amount);
        if (itemEntity != null) {
            Vector2 mwp = Util.getMouseWorldPosition();
            float distance = Util.getDistance(x, y, mwp.x, mwp.y);
            float v = Math.min(distance, 4f);
            itemEntity.setSpeed(v);
            itemEntity.setCurSpeed(v);
            itemEntity.setVelocity(getDirection().toVector2());
        }

        return itemEntity;
    }

    /**
     * 指定丢弃的物品堆叠
     * @return 返回生成的物品实体（已自动添加进世界）
     * */
    public ItemEntity dropItem (ItemStack stack) {
        Vector2 mwp = Util.getMouseWorldPosition();
        float distance = Util.getDistance(x, y, mwp.x, mwp.y);
        float v = Math.min(distance, 4f);

        return spawnItemEntity(stack)
            .setSpeed(v)
            .setCurSpeed(v)
            .setVelocity(getDirection().toVector2());
    }

    /**
     * 玩家的快速添加身体碰撞箱方法
     * */
    @Override
    public Player fastAddBodyHitBox () {
        float halfWidth = this.getWidth() / 2f;
        float halfHeight = this.getHeight() / 2f;
        //加上偏移量
        this.addRectHitBox(
            HITBOX_BODY,
            - halfWidth + HITBOX_OFFSET.x,
            - halfHeight + HITBOX_OFFSET.y,
            halfWidth + HITBOX_OFFSET.z,
            halfHeight + HITBOX_OFFSET.w
        );

        return this;
    }

    @Override
    public Direction getDirection () {
        return Util.getDirection();
    }

    @Override
    public Codec getCodec () {
        return Codecs.PLAYER;
    }
}
