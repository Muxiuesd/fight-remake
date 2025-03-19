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

/**
 * 玩家
 */
public class Player extends Entity {
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
        initialize(Group.player, maxHealth, curHealth);

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

        Log.print(this.getClass().getName(),"Player 初始化完成");
    }

    @Override
    public void update(float delta) {
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
        if (KeyBindings.PlayerShoot.wasPressed()) {
            Bullet bullet = Factory.createBullet(this, Util.getDirection());
            getEntitySystem().add(bullet);
            //AudioPlayer.getInstance().playSound("shoot");
        }
        if (KeyBindings.PlayerShield.wasPressed() && this.defendSpan >= 1f) {
            this.isDefend = true;
            this.defendSpan = 0f;
            this.defendDuration = 0f;
        }
    }
}
