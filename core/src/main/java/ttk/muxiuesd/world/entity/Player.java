package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.item.ItemsReg;

/**
 * 玩家
 */
public class Player extends LivingEntity {
    public TextureRegion body;
    public TextureRegion shield;
    public float maxDefendSpan = 1f;
    public float defendSpan = 0f;   //防御的间隔
    public float maxDefendDuration = 0.3f;
    public float defendDuration = 0f;   //防御状态的持续时间
    public boolean isDefend = false;
    public float defenseRadius = 1.23f;

    public Player () {
        this(10, 10);
    }

    public Player(float maxHealth, float curHealth) {
        initialize(Group.player, maxHealth, curHealth, 16);

        speed = 8;
        curSpeed = speed;
        setSize(1, 1);

        AssetsLoader.getInstance().loadAsync(Fight.getId("player"),
            Fight.getEntityTexture("player/player.png"),
            Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(Fight.getId("player"), Texture.class);
            textureRegion = new TextureRegion(texture);
        });
        AssetsLoader.getInstance().loadAsync(Fight.getId("player_shield"),
            Fight.getEntityTexture("player/shield.png"),
            Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(Fight.getId("player_shield"), Texture.class);
            this.shield = new TextureRegion(texture);
        });

        backpack.setItemStack(0, ItemsReg.getItem("test_item"));
        backpack.setItemStack(1, ItemsReg.getItem("stick"));
        backpack.setItemStack(2, ItemsReg.getItem("test_weapon"));

        Log.print(this.getClass().getName(),"Player 初始化完成");
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setCullingArea(x + 0.1f, y + 0.1f, width - 0.2f, height - 0.2f);

        if (!this.isDefend && this.defendSpan < this.maxDefendSpan) {
            this.defendSpan += delta;
        }
        if (this.isDefend) {
            if (this.defendDuration > this.maxDefendDuration) {
                this.isDefend = false;
                this.defendDuration = 0f;
            }
            if (this.defendDuration <= this.maxDefendDuration) {
                this.defendDuration += delta;
            }
        }
        this.handleInput(delta);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        /*ItemStack itemStack = backpack.getItemStack(getHandIndex());
        if (itemStack != null) {
            TextureRegion texture = itemStack.getItem().texture;
            if (texture != null) {
                Direction direction = Util.getDirection();
                float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection()) - 45;
                batch.draw(texture, x + getWidth() / 2, y + getHeight() / 2,
                    0, 0,
                    width, height,
                    scaleX, scaleY, rotation);
            }
        }*/

        if (this.isDefend && this.shield != null) {
            batch.draw(this.shield, x, y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
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
            Bullet bullet = Factory.createBullet(this, Util.getDirection());
            //getEntitySystem().add(bullet);
        }
        if (KeyBindings.PlayerShield.wasJustPressed() && this.defendSpan >= 1f) {
            this.isDefend = true;
            this.defendSpan = 0f;
            this.defendDuration = 0f;
        }
        if (KeyBindings.PlayerUseItem.wasJustPressed()) {
            if (useItem(getEntitySystem().getWorld())){
                System.out.println("使用成功");
            }
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
