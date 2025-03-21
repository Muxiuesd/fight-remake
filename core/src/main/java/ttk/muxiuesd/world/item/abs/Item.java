package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.LivingEntity;

import java.util.Objects;

/**
 * 物品
 * */
public abstract class Item {

    public final Property property;
    public TextureRegion texture;

    public Item (Property property, String textureId, String texturePath) {
        this.property = property;
        this.loadTextureRegion(textureId, texturePath);
    }

    /**
     * 使用此物品
     * */
    public void ues (World world, LivingEntity user) {
    }


    public void loadTextureRegion (String id) {
        this.texture = new TextureRegion(AssetsLoader.getInstance().getById(id, Texture.class));
    }

    public void loadTextureRegion (String id, String texturePath) {
        AssetsLoader.getInstance().loadAsync(id, texturePath, Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(id, Texture.class);
            this.texture = new TextureRegion(texture);
        });
    }
    public static class Property {
        private int maxCount = 64;
        private String useSoundId;

        public int getMaxCount () {
            return this.maxCount;
        }

        public Property setMaxCount (int maxCount) {
            if (maxCount > 0 ){
                this.maxCount = maxCount;
                return this;
            }
            throw new IllegalArgumentException ("最大堆叠数必须大于0！！！");
        }

        public String getUseSoundId () {
            return Objects.requireNonNullElse(useSoundId, Fight.getId("click"));
        }

        public Property setUseSoundId (String useSoundId) {
            this.useSoundId = useSoundId;
            return this;
        }
    }
}
