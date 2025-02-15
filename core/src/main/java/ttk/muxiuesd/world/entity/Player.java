package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.audio.AudioManager;
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
        this(100, 100);
    }

    public Player(float maxHealth, float curHealth) {
        initialize(Group.player, maxHealth, curHealth);

        speed = 8;
        curSpeed = speed;
        setSize(1, 1);

        AssetsLoader.getInstance().loadAsync("player/player.png", Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().get("player/player.png", Texture.class);
            textureRegion = new TextureRegion(texture);
        });
        AssetsLoader.getInstance().loadAsync("player/shield.png", Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().get("player/shield.png", Texture.class);
            this.shield = new TextureRegion(texture);
        });

        Log.print(this.getClass().getName(),"Player 初始化完成");
    }

    @Override
    public void update(float delta) {
        super.update(delta);
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
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.y = y + curSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.y = y - curSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.x = x - curSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.x = x + curSpeed * delta;
        }
        // 左键发射攻击性子弹
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Bullet bullet = Factory.createBullet(this, Util.getDirection());
            getEntitySystem().add(bullet);
            AudioManager.getInstance().playSound("shoot");
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && this.defendSpan >= 1f) {
            this.isDefend = true;
            this.defendSpan = 0f;
            this.defendDuration = 0f;
        }
    }

    /**
     * 获取玩家中心的坐标（世界坐标）
     * @return 二维坐标
     */
    public Vector2 getPlayerCenter () {
        return new Vector2(x + getWidth() / 2, y + getHeight() / 2);
    }
}
