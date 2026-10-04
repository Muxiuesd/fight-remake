package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 活物实体的渲染器
 * */
public class LivingEntityRenderer<T extends LivingEntity<?>> extends EntityRenderer.StandardRenderer<T> {
    public static final Color ATTACTED_COLOR = new Color(1f, 0f, 0f, 1f);
    public static final Resource<TextureRegion> ENTITY_SHADOW_RESOURCE = Resource.ofTextureRegion(
        Fight.ID("entity_shadow"),
        Fight.EntityTexturePath("shadow.png")
    );

    /**
     * 是否按实体朝向水平翻转贴图
     * <p>
     * 贴图是朝着某个方向画的实体（例如鸡的贴图朝右），开启后实体朝反方向时贴图会镜像绘制。
     * 默认关闭，既有实体不受影响
     * <p>
     * 这是渲染器自身的配置项，不是实体的方向状态——朝向每帧都从实体身上现取
     * */
    private boolean flipWhenFacing = false;

    /**
     * @param textureId 身体贴图资源的id（一般与实体id相同）
     * @param texturePath 身体贴图文件在 texture/entity 目录下的路径
     * */
    public LivingEntityRenderer (String textureId, String texturePath) {
        super(textureId, texturePath);
    }

    /**
     * @param textureId 身体贴图资源的id（一般与实体id相同）
     * @param texturePath 身体贴图文件在 texture/entity 目录下的路径
     * @param flippedTextureRegionResource 水平镜像的身体贴图资源，
     *                                     由 {@link Resource#ofFlippedTextureRegion} 创建，
     *                                     贴图朝右画的实体朝左时使用
     * */
    public LivingEntityRenderer (String textureId, String texturePath,
                                 Resource<TextureRegion> flippedTextureRegionResource) {
        super(Resource.ofTextureRegion(textureId, Fight.EntityTexturePath(texturePath)),
              flippedTextureRegionResource);
    }

    @Override
    public void draw (Batch batch, T entity, Context context) {
        //需要翻转朝向时把信号写进渲染上下文（贴图朝右画的实体，朝左时就镜像绘制）
        context.flipX = this.flipWhenFacing && isFacingLeft(entity);

        batch.setColor(1f, 1f, 1f, 0.666f);
        //渲染影子
        batch.draw(ENTITY_SHADOW_RESOURCE.get(),
            context.x - context.width / 2f, context.y - (context.height / 5f) - (context.height / 2f),
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY / 2f,
            context.rotation
        );
        batch.setColor(1f, 1f, 1f, 1f);

        //身体渲染
        if (!entity.isAttacked()) {
            super.draw(batch, entity, context);
        }else {
            // 受到攻击变红
            batch.setColor(ATTACTED_COLOR);
            super.draw(batch, entity, context);
            // 还原batch
            batch.setColor(1f, 1f, 1f, 1f);
        }

        //手持物品渲染
        if (entity.renderHandItem) {
            this.drawHandItem(batch, entity, context);
        }
    }

    /**
     * 判断实体当前是否朝左
     * <p>
     * 依据是实体朝向向量的水平分量（也就是朝向与x轴夹角的余弦）的符号：朝右为正、朝左为负。
     * 用实体的 getDirection() 而不是速度：大部分实体的朝向就是它的速度朝向，
     * 但有些实体（例如玩家）的朝向由别的规则决定，朝向的语义归实体自己管
     * <p>
     * 静止时方向是零向量（{@code Direction.nor()} 对零向量不做归一化，直接把分量置0），
     * 水平分量为0时这里判定为false，也就是转回贴图默认朝向的那一侧（朝右）
     * <p>
     * 只取水平分量而不是完整的cos值——斜向移动时仍然按主要朝向翻面
     * */
    private static boolean isFacingLeft (LivingEntity<?> entity) {
        return entity.getDirection().getX() < 0f;
    }

    /**
     * 是否按实体朝向翻转贴图
     * */
    public boolean isFlipWhenFacing () {
        return this.flipWhenFacing;
    }

    /**
     * 设置是否按实体朝向翻转贴图，返回自身便于链式配置
     * <p>
     * 需要同时给渲染器配置水平镜像的贴图资源，否则镜像时没有贴图可画
     * */
    public LivingEntityRenderer<T> setFlipWhenFacing (boolean flipWhenFacing) {
        this.flipWhenFacing = flipWhenFacing;
        return this;
    }

    /**
     * 手上持有的物品绘制，普通的手上持有物品的实体，物品渲染方向朝向它的运动方向
     * */
    public void drawHandItem (Batch batch, T entity, Context context) {
        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = entity.getHandItemStack();
        if (!itemStack.isVoid()) {
            //获取物品的渲染器来渲染
            ItemRenderer<Item> itemRenderer = ItemRendererRegistry.get(itemStack.getItem());
            if (itemRenderer == null) return;

            ItemRenderer.Context itemContext = itemRenderer.getContextByEntityContext(context);
            //物品渲染起点基于实体中心
            itemContext.x += context.width / 2f;
            itemContext.y += context.height / 2f;
            //获取实体的指向方向（玩家实体的方向为玩家在世界上的坐标指向鼠标在世界上的坐标的向量）
            Direction direction = entity.getDirection();
            itemContext.rotation = MathUtils.atan2Deg360(direction.getY(), direction.getX());
            itemRenderer.drawOnHand(batch, itemContext, entity, itemStack);
            itemRenderer.freeContext(itemContext);
        }
    }


    @Override
    public void drawShape (ShapeRenderer batch, T entity, Context context) {
        if (entity.renderHandItem) this.renderShapeHandItem(batch, entity);
    }

    /**
     * 持有物品的形状渲染
     * */
    public void renderShapeHandItem (ShapeRenderer batch, T entity) {
        ItemStack itemStack = entity.getHandItemStack();
        if (!itemStack.isVoid()) {
            //获取物品的渲染器来渲染
            ItemRenderer<Item> renderer = ItemRendererRegistry.get(itemStack.getItem());
            if (renderer == null) return;
            renderer.renderShapeOnHand(batch, entity, itemStack);
        }
    }
}
