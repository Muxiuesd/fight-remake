package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.TextureUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Util;

/**
 * 滚动条UI组件
 * <p>
 * （垂直轨道起点在最上面，横向轨道起点在最左边）
 * （垂直轨道终点在最下面，横向轨道终点在最右边）
 * */
public class UIScrollBar extends UIComponent {
    public static final float DEFAULT_SLIDER_WIDTH = 12f, DEFAULT_SLIDER_HEIGHT = 15f;
    //滑块点9属性
    public static final int SLIDER_LEFT = 2, SLIDER_RIGHT = 2, SLIDER_TOP = 2, SLIDER_BOTTOM = 2;
    //滑轨点9属性
    public static final int SLIDE_WAY_LEFT = 1, SLIDE_WAY_RIGHT = 1, SLIDE_WAY_TOP = 1, SLIDE_WAY_BOTTOM = 1;

    //滚动条种类
    public enum Type {
        /// 竖直的
        VERTICAL,
        /// 横向的
        HORIZONTAL
    }

    private Type type;
    private NinePatch slideWayPatch;    //滑轨
    private NinePatch sliderPatch;      //滑块
    private float sliderX, sliderY, sliderWidth, sliderHeight;  //滑块的坐标（相当对于滚动条的坐标）和宽高
    private boolean sliderVisible = true, sliderWayVisible = true;

    /**
     * 默认的滚动条构造方法
     * */
    public UIScrollBar() {
        this(
            Fight.ID("slider"), Fight.UITexturePath("slider.png"),
            Fight.ID("slide_way"), Fight.UITexturePath("slideway.png"),
            12f, 110f, Type.VERTICAL
        );
    }
    public UIScrollBar(String sliderPatchId, String sliderTexturePath,
                       String sliderWayPatchId, String sliderWayTexturePath,
                       float width, float height, Type type) {
        this(
            TextureUtil.createNinePatch(
                Util.loadTextureRegion(sliderPatchId, sliderTexturePath),
                SLIDER_LEFT, SLIDER_RIGHT, SLIDER_TOP, SLIDER_BOTTOM
            ),
            TextureUtil.createNinePatch(
                Util.loadTextureRegion(sliderWayPatchId, sliderWayTexturePath),
                SLIDE_WAY_LEFT, SLIDE_WAY_RIGHT, SLIDE_WAY_TOP, SLIDE_WAY_BOTTOM
            ),
            width, height, type
        );
    }
    public UIScrollBar(NinePatch sliderPatch, NinePatch sliderWayPatch,
                       float width, float height, Type type) {
        this(0, 0, width, height, new GridPoint2((int) width, (int) height), sliderPatch, sliderWayPatch, type);
    }
    public UIScrollBar(float x, float y,
                       float width, float height,
                       GridPoint2 interactGridSize,
                       NinePatch sliderPatch, NinePatch slideWayPatch,Type type) {
        super(x, y, width, height, interactGridSize);
        this.sliderPatch = sliderPatch;
        this.slideWayPatch = slideWayPatch;
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
    public void mouseDrag (float mouseX, float mouseY) {
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

        //System.out.println(this.getSliderPathwayPos());
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        //绘制轨道
        if (this.isSliderWayVisible()) {
            this.getSlideWayPatch()
                .draw(batch, getAbsX(), getAbsY(), getWidth(), getHeight());
        }

        //绘制滑块
        if (this.isSliderVisible()) {
            float renderX = getSliderX() + getAbsX();
            float renderY = getSliderY() + getAbsY();
            this.getSliderPatch()
                .draw(batch, renderX, renderY, getSliderWidth(), getSliderHeight());
        }
    }

    public Type getType () {
        return this.type;
    }

    public UIScrollBar setType (Type type) {
        this.type = type;
        return this;
    }

    public NinePatch getSlideWayPatch () {
        return this.slideWayPatch;
    }

    public UIScrollBar setSlideWayPatch (NinePatch slideWayPatch) {
        this.slideWayPatch = slideWayPatch;
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

    public boolean isSliderVisible () {
        return this.sliderVisible;
    }

    public boolean isSliderWayVisible () {
        return this.sliderWayVisible;
    }

    public UIScrollBar setSliderVisible (boolean sliderVisible) {
        this.sliderVisible = sliderVisible;
        return this;
    }

    public UIScrollBar setSliderWayVisible (boolean sliderWayVisible) {
        this.sliderWayVisible = sliderWayVisible;
        return this;
    }
}
