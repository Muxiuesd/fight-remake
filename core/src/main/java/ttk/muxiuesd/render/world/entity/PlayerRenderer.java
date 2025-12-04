package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.world.entity.Player;

/**
 * 玩家的渲染器
 * */
public class PlayerRenderer extends LivingEntityRenderer<Player> {
    @Override
    public void draw (Batch batch, Player player, Context context) {
        super.draw(batch, player, context);

        //绘制护盾
        if (player.isDefend && player.shield != null) {
            batch.draw(player.shield, context.x, context.y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }
    }

    @Override
    public void drawShape (ShapeRenderer batch, Player entity, Context context) {
        super.drawShape(batch, entity, context);
    }
}
