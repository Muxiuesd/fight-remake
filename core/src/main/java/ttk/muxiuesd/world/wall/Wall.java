package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.interfaces.render.ShapeRenderable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.hitbox.Hitbox;
import ttk.muxiuesd.world.hitbox.HitboxHolder;
import ttk.muxiuesd.world.hitbox.RectHitbox;

/**
 * 墙体，即有碰撞箱的特殊方块，目前也是一个墙体一个实例
 * */
public abstract class Wall<T extends Wall<T>> extends Block implements ShapeRenderable {
    public static final String DEFAULT_HITBOX_ID = Fight.ID("wall");

    /**
     * 墙体的现代化编解码器
     * <p>
     * 解码时通过墙体注册表拿到原型，再调用{@link #createSelf}创建一个新的实例
     * */
    public static final Codec<Wall<?>> CODEC = CodecBuilder.<Wall<?>>create()
        .paramField("id", Wall::getID, Codec.STRING)
        .paramField("x", wall -> wall.x, Codec.FLOAT)
        .paramField("y", wall -> wall.y, Codec.FLOAT)
        .field("property", Wall::getProperty, Wall::setProperty, Block.Property.CODEC)
        .factory((id, x, y) -> {
            Wall<?> prototype = Registries.WALL.get(id);
            Wall<?> self = prototype.createSelf(new Vector2(x, y));
            self.setID(id);
            return self;
        });

    public float x, y;
    private RectHitbox rectHitbox;  //普通墙体只有一个矩形碰撞箱，最好也只有一个
    private HitboxHolder<Wall<T>> hitboxHolder;

    public Wall(Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);

        this.rectHitbox = new RectHitbox()
            .setStartPos(HITBOX_START_X_OFFSET, HITBOX_START_Y_OFFSET)
            .setEndPos(HITBOX_END_X_OFFSET, HITBOX_END_Y_OFFSET);
        this.hitboxHolder = new HitboxHolder<>(this);
        this.hitboxHolder.addBox(DEFAULT_HITBOX_ID, this.rectHitbox);
    }

    /**
     * 创建一个墙体实例
     * */
    public abstract T createSelf (Vector2 position);

    @Override
    public void renderShape(ShapeRenderer batch) {
        Rectangle hitboxRectangle = this.getHitboxRectangle();
        batch.rect(hitboxRectangle.x, hitboxRectangle.y, hitboxRectangle.width, hitboxRectangle.height);
    }

    public Rectangle getHitboxRectangle () {
        return this.getRectHitbox().getRectangle();
    }

    /**
     * 设置默认的hitbox，以墙体的中心点的世界坐标
     * */
    public Wall<T> setHitbox () {
        return this.setHitbox(x, y);
    }

    public Wall<T> setHitbox (float cx, float cy) {
        Hitbox box = this.getHitboxHolder().getBox(DEFAULT_HITBOX_ID);
        box.setCenterPos(cx, cy);
        return this;
    }

    /**
     * 设置墙体的坐标
     * @param position 传入世界坐标
     * */
    public void setPos (Vector2 position) {
        this.setPos(position.x, position.y);
    }

    /**
     * 传入世界坐标，墙体的中心会置于这个坐标上，默认渲染也会基于这个坐标为中心渲染贴图
     * */
    public void setPos (float x, float y) {
        this.x = x;
        this.y = y;
        this.setHitbox();
    }

    public RectHitbox getRectHitbox () {
        return this.rectHitbox;
    }

    public Wall<T> setRectHitbox (RectHitbox rectHitbox) {
        this.rectHitbox = rectHitbox;
        return this;
    }

    public HitboxHolder<Wall<T>> getHitboxHolder () {
        return this.hitboxHolder;
    }

    public Wall<T> setHitboxHolder (HitboxHolder<Wall<T>> hitboxHolder) {
        if (hitboxHolder != null) this.hitboxHolder = hitboxHolder;
        return this;
    }
}
