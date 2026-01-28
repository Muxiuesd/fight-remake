package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;

/**
 * 滚动条UI组件
 * */
public class UIScrollBar extends UIComponent {
    public static final int SLIDER_LEFT = 2, SLIDER_RIGHT = 2, SLIDER_TOP = 2, SLIDER_BOTTOM = 3;
    //滚动条种类
    public enum Type {
        VERTICAL, //竖直的
        HORIZONTAL //横向的
    }

    private Type type;
    private NinePatch sliderPatch;
    private float sliderX, sliderY, sliderWidth, sliderHeight;

    public UIScrollBar() {
        this(Fight.ID("slider"), Fight.UITexturePath("slider.png"), 12f, 100f, Type.VERTICAL);
    }
    public UIScrollBar(String sliderPatchId, String sliderTexturePath, float width, float height, Type type) {
        this(
            createNinePatch(
                Util.loadTextureRegion(sliderPatchId, sliderTexturePath),
                SLIDER_LEFT, SLIDER_RIGHT, SLIDER_TOP, SLIDER_BOTTOM
            ),
            width, height, type
        );
    }
    public UIScrollBar(NinePatch sliderPatch, float width, float height, Type type) {
        this(0, 0, width, height, new GridPoint2((int) width, (int) height), sliderPatch, type);
    }
    public UIScrollBar(float x, float y,
                       float width, float height,
                       GridPoint2 interactGridSize,
                       NinePatch sliderPatch, Type type) {
        super(x, y, width, height, interactGridSize);
        this.sliderPatch = sliderPatch;
    }

    @Override
    public void mouseDrag (float dx, float dy) {
         if (this.getType() == Type.VERTICAL) {
             float nextY = getSliderY() + dy;
             //计算滑块底部坐标，确保不能超过滚动条的底部
             if (nextY < getY()) {
                 setSliderY(getY());
             }
             //计算滑块顶部坐标，确保不能超过滚动条的顶部
             float sliderTopY = nextY + this.getSliderHeight();
             float scrollTopY = getY() + getHeight();
             if (sliderTopY > scrollTopY) {
                 setSliderY(scrollTopY - this.getSliderHeight());
             }
             setSliderY(nextY);
         }else {
             //TODO 横向的逻辑
             setX(getX() + dx);
         }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {

    }

    public Type getType () {
        return this.type;
    }

    public UIScrollBar setType (Type type) {
        this.type = type;
        return this;
    }

    public NinePatch getSliderPatch () {
        return this.sliderPatch;
    }

    public UIScrollBar setSliderPatch (NinePatch sliderPatch) {
        this.sliderPatch = sliderPatch;
        return this;
    }

    public float getSliderX () {
        return this.sliderX;
    }

    public UIScrollBar setSliderX (float sliderX) {
        this.sliderX = sliderX;
        return this;
    }

    public float getSliderY () {
        return this.sliderY;
    }

    public UIScrollBar setSliderY (float sliderY) {
        this.sliderY = sliderY;
        return this;
    }

    public float getSliderWidth () {
        return this.sliderWidth;
    }

    public UIScrollBar setSliderWidth (float sliderWidth) {
        this.sliderWidth = sliderWidth;
        return this;
    }

    public float getSliderHeight () {
        return this.sliderHeight;
    }

    public UIScrollBar setSliderHeight (float sliderHeight) {
        this.sliderHeight = sliderHeight;
        return this;
    }
}
