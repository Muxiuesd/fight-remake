package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.serialization.Codec;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.StatusEffects;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家
 */
public class Player extends LivingEntity<Player> {
    public static final int BACKPACK_SIZE = 36;
    public static final Vector4 HITBOX_OFFSET = new Vector4(0.1f, 0.1f, 0.2f, 0.2f);


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
        speed = 8;
        curSpeed = speed;
        bodyTexture = getTextureRegion(Fight.ID("player"), "player/player.png");
        this.shield = getTextureRegion(Fight.ID("player_shield"), "player/shield.png");

        this.defendCDTimer = Pools.TASK_TIMER.obtain().setMaxSpan(2f).setCurSpan(0f)
            .setTask(() -> this.isDefend = true);
        this.defendDurationTimer = Pools.TASK_TIMER.obtain().setMaxSpan(0.3f).setCurSpan(0f)
            .setTask(() ->  {
                //到时间了就取消防御状态
                this.isDefend = false;
            });


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

        setEffect(StatusEffects.HEALING, 20f, 2);

        Log.print(this.getClass().getName(),"Player 初始化完成");
    }

    @Override
    public void readCAT (JsonValue values) {
        super.readCAT(values);

        //更新hitbox
        Vector2 position = getPosition();
        setCullingArea(
            position.x + HITBOX_OFFSET.x,
            position.y + HITBOX_OFFSET.y,
            getWidth() - HITBOX_OFFSET.z,
            getHeight() - HITBOX_OFFSET.w
        );
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
        this.handleInput(delta);

        Vector2 position = getPosition();
        setCullingArea(
            position.x + HITBOX_OFFSET.x,
            position.y + HITBOX_OFFSET.y,
            getWidth() - HITBOX_OFFSET.z,
            getHeight() - HITBOX_OFFSET.w
        );
        //setCullingArea(x, y, width, height);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (this.isDefend && this.shield != null) {
            batch.draw(this.shield, x, y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
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
            itemEntity.setVelocity(getDirection());
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
            .setVelocity(getDirection());
    }

    @Override
    public Direction getDirection () {
        return Util.getDirection();
    }

    private void handleInput(float delta) {
        //需要玩家鼠标不在UI组件上，防止同时操作两者
        if (!GUISystem.getInstance().mouseOverUI()) {
            //玩家右键防御
            if (KeyBindings.PlayerShield.wasJustPressed()) {
                this.defendCDTimer.isReady();
                //TODO 护盾使用成功的相关操作
            }
            //左键使用物品
            if (KeyBindings.PlayerUseItem.wasJustPressed()) {
                useItem(getEntitySystem().getWorld());
            }
        }

        //头两个物品槽（0号和1号）快捷循环
        if (KeyBindings.PlayerChangeItem.wasJustPressed()) {
            if (getHandIndex() == 0) setHandIndex(1);
            else if (getHandIndex() == 1) setHandIndex(0);
        }
        if (KeyBindings.PlayerDropItem.wasJustPressed()) {
            dropItem(getHandIndex(), 1);
            System.out.println("Q");
        }
    }

    @Override
    public Codec getCodec () {
        return Codecs.PLAYER;
    }
}
