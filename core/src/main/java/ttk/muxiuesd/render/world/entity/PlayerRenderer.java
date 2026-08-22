package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 玩家的渲染器
 * */
public class PlayerRenderer extends LivingEntityRenderer<Player> {
    //护盾贴图资源（玩家不持有贴图，由渲染器持有）
    public static final Resource<TextureRegion> SHIELD_TEXTURE_RESOURCE = Resource.ofTextureRegion(
        Fight.ID("player_shield"),
        Fight.EntityTexturePath("player/shield.png")
    );

    public PlayerRenderer () {
        super(Fight.ID("player"), "player/player.png");
    }

    @Override
    public void draw (Batch batch, Player player, Context context) {
        super.draw(batch, player, context);
        //绘制护盾
        if (player.isDefend) {
            TextureRegion shieldTextureRegion = SHIELD_TEXTURE_RESOURCE.get();
            if (shieldTextureRegion != null) {
                batch.draw(shieldTextureRegion,
                    context.x - context.width / 2f, context.y - context.height / 2f,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    context.rotation);
            }
        }
    }

    @Override
    public void drawShape (ShapeRenderer batch, Player entity, Context context) {
        super.drawShape(batch, entity, context);
    }
}
