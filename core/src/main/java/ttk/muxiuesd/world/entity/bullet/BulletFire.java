package ttk.muxiuesd.world.entity.bullet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.world.entity.Group;

public class BulletFire extends Bullet {
    //public static TextureRegion img = new TextureRegion(AssetsLoader.getInstance().get("bullet/flame.png", Texture.class));

    public BulletFire(Group group) {
        super(group, 1, 1);

        setSize(0.5f, 0.5f);
        damage = 1f;
        setMaxLiveTime(3f);
        setLiveTime(0f);
        speed = 15f;

        AssetsLoader.getInstance().loadAsync("bullet/flame.png", Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().get("bullet/flame.png", Texture.class);
            textureRegion = new TextureRegion(texture);
        });
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        setLiveTime(getLiveTime() + delta);
        this.x = x + speed * delta * xDirection;
        this.y = y + speed * delta * yDirection;
    }

}
