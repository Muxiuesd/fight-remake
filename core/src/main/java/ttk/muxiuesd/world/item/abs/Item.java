package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

import java.util.Objects;

/**
 * 物品
 * */
public abstract class Item implements ID, Updateable,ShapeRenderable {
    private String id;

    public Type type;
    public Property property;
    public TextureRegion texture;


    /*public Item () {
    }*/
    public Item (Type type, Property property, String textureId) {
        this(type, property, textureId, null);
    }
    public Item (Type type, Property property, String textureId, String texturePath) {
        this.type = type;
        this.property = property;
        this.loadTextureRegion(textureId, texturePath);
    }


    /**
     * 在持有者手上持有时的绘制方法
     * TODO 不同类型的物品不同的绘制方式
     * */
    public void drawOnHand (Batch batch, LivingEntity holder) {
        if (this.texture != null) {
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection()) - 45;
            batch.draw(this.texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                0, 0,
                holder.width, holder.height,
                holder.scaleX, holder.scaleY, rotation);
        }
    }

    /**
     * 在掉落物形式下的绘制方法
     * @param itemEntity 所属的物品实体
     * */
    public void drawOnWorld (Batch batch, ItemEntity itemEntity) {
        if (this.texture != null) {
            batch.draw(this.texture, itemEntity.x, itemEntity.y + itemEntity.getPositionOffset().y,
                itemEntity.originX, itemEntity.originY,
                itemEntity.width, itemEntity.height,
                itemEntity.scaleX, itemEntity.scaleY, itemEntity.rotation);
        }
    }

    @Override
    public void update (float delta) {
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
    }

    /**
     * 使用此物品
     * @return 是否使用成功
     * */
    public boolean use (World world, LivingEntity user) {
        //播放物品使用音效
        String useSoundId = this.property.getUseSoundId();
        SoundEffectSystem ses = (SoundEffectSystem)world.getSystemManager().getSystem("SoundEffectSystem");
        ses.newSpatialSound(useSoundId, user);

        return true;
    }

    /**
     * 获取材质
     * <p>
     * 有返回值，以便于有多个材质的物品使用
     * @param texturePath 当此为null时默认之前加载过
     * */
    public TextureRegion getTextureRegion (String id, String texturePath) {
        if (texturePath == null) {
            texturePath = AssetsLoader.getInstance().getPath(id);
        }

        AssetsLoader.getInstance().loadAsync(id, texturePath, Texture.class, null);
        return new TextureRegion(AssetsLoader.getInstance().getById(id, Texture.class));
    }

    /**
     * 直接把物品的texture加载并赋值
     * */
    public void loadTextureRegion (String id, String texturePath) {
        this.texture = this.getTextureRegion(id, texturePath);
    }

    @Override
    public String getID () {
        return this.id;
    }
    @Override
    public void setID (String id) {
        this.id = id;
    }

    /**
     * 物品的类型
     * */
    public enum Type {
        COMMON,         //普通物品
        CONSUMPTION,    //消耗品
        WEAPON,         //武器
        EQUIPMENT       //装备
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
