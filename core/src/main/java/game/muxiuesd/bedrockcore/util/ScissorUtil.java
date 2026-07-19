package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

/**
 * 渲染裁剪工具，指定渲染的时候只在某些区域内渲染出图形
 * */
public class ScissorUtil {
    static boolean popped = false;
    /**
     * 开始指定矩形区域的裁剪
     * @param batch 当前的batch
     * @param camera batch对应的相机
     */
    public static void beginScissor (Batch batch, Camera camera, float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) return;

        batch.flush();
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(x, y, width, height);

        // 计算裁剪区域
        ScissorStack.calculateScissors(
            camera,
            batch.getTransformMatrix(),
            clipBounds,
            scissors
        );

        // 压入裁剪栈
        ScissorStack.pushScissors(scissors);
        popped = true;
    }

    /**
     * 结束裁剪
     */
    public static void endScissor (Batch batch) {
        if (!popped) return;

        batch.flush();
        ScissorStack.popScissors();
        popped = false;
    }
}
