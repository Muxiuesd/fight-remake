package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.util.*;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家
 */
public class Player extends LivingEntity {
    public TextureRegion body;
    public TextureRegion shield;
    public Timer defendCDTimer; //防御状态冷却计时器
    public Timer defendDurationTimer; //防御状态持续计时器
    public boolean isDefend = false;
    public float defenseRadius = 1.23f; //防御半径

    public Player () {
        this(10, 10);
    }

    public Player(float maxHealth, float curHealth) {
        initialize(Group.player, maxHealth, curHealth, 16);

        speed = 8;
        curSpeed = speed;
        setSize(1, 1);
        bodyTexture = getTextureRegion(Fight.getId("player"), "player/player.png");

        this.shield = getTextureRegion(Fight.getId("player_shield"), "player/shield.png");
        this.defendCDTimer = new Timer(2f, 0);
        this.defendDurationTimer = new Timer(0.3f, 0);


        backpack.setItemStack(0, new ItemStack(Items.TEST_ITEM));
        backpack.setItemStack(1, new ItemStack(Items.TEST_WEAPON));
        backpack.setItemStack(2, new ItemStack(Items.STICK));
        backpack.setItemStack(3, new ItemStack(Items.FURNACE));
        backpack.setItemStack(4, new ItemStack(Items.CRAFTING_TABLE));
        backpack.setItemStack(5, new ItemStack(Items.FISH_POLE));
        backpack.setItemStack(6, new ItemStack(Items.COAL_ORE));
        //backpack.setItemStack(6, new ItemStack(Items.FISH, 2));
        backpack.setItemStack(7, new ItemStack(Items.FISH, 1));

        //backpack.setItemStack(6, new ItemStack(Gets.ITEM(Fight.getId("bait"))));

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
                //到时间了就取消防御状态
                this.isDefend = false;
            }else {
                //没到时间就继续计时
                this.defendDurationTimer.update(delta);
            }
        }
        this.handleInput(delta);
        setCullingArea(x + 0.1f, y + 0.1f, width - 0.2f, height - 0.2f);
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
    public boolean dropItem (int index, int amount) {
        ItemStack itemStack = this.backpack.dropItem(index, amount);
        if (itemStack == null) return false;

        ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Fight.getId("item_entity"), getEntitySystem());
        itemEntity.setPosition(getPosition());
        itemEntity.setItemStack(itemStack);
        itemEntity.setOnGround(false);
        itemEntity.setOnAirTimer(new TaskTimer(0.3f, 0, () -> itemEntity.setOnAirTimer(null)));
        itemEntity.setVelocity(Util.getDirection());
        itemStack.getItem().beDropped(itemStack, getEntitySystem().getWorld(), this);

        HandleInputSystem his = (HandleInputSystem) getEntitySystem().getWorld().getSystemManager().getSystem("HandleInputSystem");
        Vector2 mwp = his.getMouseWorldPosition();
        float distance = Util.getDistance(x, y, mwp.x, mwp.y);
        float v = Math.min(distance, 4f);
        itemEntity.setSpeed(v);
        itemEntity.setCurSpeed(v);

        AudioPlayer.getInstance().playSound(Fight.getId("pop"));

        return true;
    }

    @Override
    public Direction getDirection () {
        return Util.getDirection();
    }

    private void handleInput(float delta) {
        if (KeyBindings.PlayerWalkUp.wasPressed()) {
            //this.y = y + curSpeed * delta;
            velY += curSpeed * delta;
        }
        if (KeyBindings.PlayerWalkDown.wasPressed()) {
            //this.y = y - curSpeed * delta;
            velY -= curSpeed * delta;
        }
        if (KeyBindings.PlayerWalkLeft.wasPressed()) {
            //this.x = x - curSpeed * delta;
            velX -= curSpeed * delta;
        }
        if (KeyBindings.PlayerWalkRight.wasPressed()) {
            //this.x = x + curSpeed * delta;
            velX += curSpeed * delta;
        }

        // 左键发射攻击性子弹
        if (KeyBindings.PlayerShoot.wasJustPressed()) {
            Bullet bullet = EntityFactory.createFireBullet(this, Util.getDirection());
            //getEntitySystem().add(bullet);
        }
        if (KeyBindings.PlayerShield.wasJustPressed() && this.defendCDTimer.isReady()) {
            this.isDefend = true;
        }
        if (KeyBindings.PlayerUseItem.wasJustPressed()) {
            useItem(getEntitySystem().getWorld());
        }
        //头两个物品槽（0号和1号）快捷循环
        if (KeyBindings.PlayerChangeItem.wasJustPressed()) {
            if (getHandIndex() == 0) setHandIndex(1);
            else if (getHandIndex() == 1) setHandIndex(0);
        }
        if (KeyBindings.PlayerDropItem.wasJustPressed()) {
            dropItem(getHandIndex(), 1);
        }
    }
}
