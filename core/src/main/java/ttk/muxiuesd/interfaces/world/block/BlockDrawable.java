package ttk.muxiuesd.interfaces.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface BlockDrawable {
    /**
     * @param x 方块需要被绘制的横坐标
     * @param y 方块需要被绘制的纵坐标
     * */
    void draw (Batch batch, float x, float y);
}
