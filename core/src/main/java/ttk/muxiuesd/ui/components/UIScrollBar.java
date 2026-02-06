package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.ui.abs.UIComponent;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;

/**
 * 滚动条UI组件
 * <p>
 * （垂直轨道起点在最上面，横向轨道起点在最左边）
 * （垂直轨道终点在最下面，横向轨道终点在最右边）
 * */
public class UIScrollBar extends UIComponent {
    public static final int SLIDER_LEFT = 2, SLIDER_RIGHT = 2, SLIDER_TOP = 2, SLIDER_BOTTOM = 3;
    //滚动条种类
    public enum Type {
        VERTICAL, //竖直的
        HORIZONTAL //横向的
    }

    private Type type;
    private NinePatch backgroundPatch;
    private NinePatch sliderPatch;
    private float sliderX, sliderY, sliderWidth, sliderHeight;  //滑块的坐标（相当对于滚动条的坐标）和宽高

    public UIScrollBar() {
        this(
            Fight.ID("slider"), Fight.UITexturePath("slider.png"),
            12f, 110f, Type.VERTICAL
        );
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
        this.type = type;
        switch (this.type) {
            case VERTICAL: {
                this.setSliderWidth(width);
                break;
            }
            case HORIZONTAL: {
                this.setSliderHeight(height);
                break;
            }
            default: {
                //TODO 错误处理
                Log.error(this.getClass().getName(), "滚动条UI种类：" + type + " 错误！！！");
            }
        }
    }

    /**
     * 获取滑块在轨道上的坐标
     * <p>
     * 以起点和终点为长度，返回值的区间在[0, 1]
     * */
    public float getSliderPathwayPos () {
        float value = 0f;
        switch (this.getType()) {
            case VERTICAL: {
                value = 1f - (this.getSliderY()) / (getHeight() - this.getSliderHeight()) ;
                break;
            }
            case HORIZONTAL: {
                value = (this.getSliderX()) / (getWidth() - this.getSliderWidth()) ;
                break;
            }
        }
        return value;
    }

    /**
     * 滑块回到滚动条的轨道起点
     * */
    public UIScrollBar sliderGotoStart () {
        switch (this.getType()) {
            case VERTICAL: {
                this.setSliderY(getHeight() - this.getSliderHeight());
                break;
            }
            case HORIZONTAL: {
                this.setSliderX(0f);
                break;
            }
        }
        return this;
    }

    /**
     * 滑块去到滚动条的轨道终点
     * */
    public UIScrollBar sliderGotoEnd () {
        switch (this.getType()) {
            case VERTICAL: {
                this.setSliderY(0f);
                break;
            }
            case HORIZONTAL: {
                this.setSliderX(getWidth() - this.getSliderWidth());
                break;
            }
        }
        return this;
    }

    /**
     * 滑块拖拽的核心算法
     * */
    @Override
    public void mouseDrag (float dx, float dy, float mouseX, float mouseY) {
        /*if (this.getType() == Type.VERTICAL) {
            float nextY = getSliderY() + dy;
            //计算滑块顶部坐标，确保不能超过滚动条的顶部
            float sliderTopY = nextY + this.getSliderHeight();
            if (sliderTopY > getHeight()) {
                setSliderY(getHeight() - this.getSliderHeight());
            }else if (nextY < 0) {
                //计算滑块底部坐标，确保不能超过滚动条的底部
                setSliderY(0);
            }else {
                setSliderY(nextY);
            }
        }else if (this.getType() == Type.HORIZONTAL) {
            setX(getX() + dx);
        }*/
        float sl = this.getSliderWidth();
        float sh = this.getSliderHeight();
        switch (this.getType()) {
            case VERTICAL: {
                float nextY = mouseY - sh / 2;
                if (nextY < 0f) {
                    this.setSliderY(0f);
                }else if (nextY + sh > getHeight()) {
                    this.setSliderY(getHeight() - sh);
                }else {
                    this.setSliderY(nextY);
                }
                break;
            }
            case HORIZONTAL: {
                float nextX = mouseX - sl / 2;
                if (nextX < 0f) {
                    this.setSliderX(0f);
                }else if (nextX + sl > getWidth()) {
                    this.setSliderX(getWidth() - sl);
                }else {
                    this.setSliderX(nextX);
                }
                break;
            }
        }

        System.out.println(this.getSliderPathwayPos());
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        //TODO 绘制滚动条的背景（假如有的话）

        //绘制滑块
        NinePatch patch = this.getSliderPatch();
        if (patch != null) {
            float renderX = getSliderX() + getAbsX();
            float renderY = getSliderY() + getAbsY();
            patch.draw(batch, renderX, renderY, getSliderWidth(), getSliderHeight());
        }
    }

    public Type getType () {
        return this.type;
    }

    public UIScrollBar setType (Type type) {
        this.type = type;
        return this;
    }

    public NinePatch getBackgroundPatch () {
        return this.backgroundPatch;
    }

    public UIScrollBar setBackgroundPatch (NinePatch backgroundPatch) {
        this.backgroundPatch = backgroundPatch;
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

    public Vector2 getSliderPos () {
        return new Vector2(this.getSliderX(), this.getSliderY());
    }

    public float getSliderY () {
        return this.sliderY;
    }

    public UIScrollBar setSliderY (float sliderY) {
        this.sliderY = sliderY;
        return this;
    }

    public Vector2 getSliderSize () {
        return new Vector2(this.getSliderWidth(), this.getSliderHeight());
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
