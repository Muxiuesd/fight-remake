package ttk.muxiuesd.world.entity.bullet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.world.entity.Entity;

public class BulletFire extends Bullet {
    //public static TextureRegion img = new TextureRegion(AssetsLoader.getInstance().get("bullet/flame.png", Texture.class));

    public BulletFire(Entity owner) {
        super(owner, 1, 1);

        setSize(0.5f, 0.5f);
        damage = 1f;
        setMaxLiveTime(3f);
        setLiveTime(0f);
        speed = 15f;

        AssetsLoader.getInstance().loadAsync(Fight.getId("bullet_fire"),
            Fight.getEntityTexture("bullet/flame.png"),
            Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(Fight.getId("bullet_fire"), Texture.class);
            textureRegion = new TextureRegion(texture);
        });
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        setLiveTime(getLiveTime() + delta);
        this.x = x + speed * delta * velX;
        this.y = y + speed * delta * velY;
    }

}
