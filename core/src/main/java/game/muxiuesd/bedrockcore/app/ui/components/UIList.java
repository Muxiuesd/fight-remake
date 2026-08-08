package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.interfaces.ui.UIListItem;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.ScissorUtil;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.util.pool.PoolableRectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表组件
 * */
public class UIList extends UIPanel {
    public static final float DEFAULT_WIDTH = 100, DEFAULT_HEIGHT = 200;
    public static final float DEFAULT_SCROLL_BAR_X = DEFAULT_WIDTH - UIScrollBar.DEFAULT_SLIDER_WIDTH;


    private List<UIListItem> items;
    private UIScrollBar scrollbar;

    public UIList () {
        this(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT));
    }
    public UIList (float width, float height,
                   GridPoint2 interactGridSize) {
        this(0, 0, width, height, interactGridSize);
    }
    public UIList (float x, float y,
                   float width, float height,
                   GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.items = new ArrayList<>();

        //默认是竖向列表，所以滚动条也是竖向的
        this.scrollbar = new UIScrollBar().setType(UIScrollBar.Type.VERTICAL);
        this.scrollbar.setHeight(height);
        this.scrollbar
            .setSliderWidth(12f)
            .setSliderHeight(15f)
            .sliderGotoStart();

        //滚动条贴着列表的右边
        this.scrollbar.setPosition(width - this.scrollbar.getWidth(), 0f);

        addComponent(this.scrollbar);
    }

    /**
     * 添加一个项目
     * */
    public UIList addItem (UIListItem item) {
        if (!this.getItems().contains(item)) {
            this.getItems().add(item);
            if (item instanceof UIComponent uiComponent) {
                addComponent(uiComponent);
            }
        }else {
            Log.error(this.getClass().getName(), "项目：" + item.getClass().getName() + " 已存在，不可重复添加！！！");
        }
        return this;
    }

    /**
     * 移除一个项目
     * */
    public UIList removeItem (UIListItem item) {
        this.getItems().remove(item);
        if (item instanceof UIComponent uiComponent) {
            removeComponent(uiComponent);
        }
        return this;
    }

    /**
     * 清空所有项目
     * */
    public UIList clearItems() {
        this.getItems().forEach((listItem)-> {
            //走标准移除路径，清理 screen/parentPanel 引用
            if (listItem instanceof UIComponent uiComponent) {
                removeComponent(uiComponent);
            }
        });

        this.getItems().clear();

        return this;
    }

    @Override
    public void update (float delta) {
        super.update(delta);

        this.calculateItemsPos();
    }

    /**
     * 计算列表里的项目在列表面板里的相对坐标
     * */
    public void calculateItemsPos () {
        List<UIListItem> itemList = this.getItems();
        for (int i = 0; i < itemList.size(); i++) {
            UIListItem item = itemList.get(i);
            if (i == 0) {
                //根据滑块的位置来计算每一个项目的渲染坐标，只要计算第一个项目，剩下的根据前一个的坐标来计算
                float sliderPathwayPos = this.getScrollbar().getSliderPathwayPos();
                Vector2 maxSize = calculateMaxSize();
                UIListItem firstItem = itemList.get(0);
                if (firstItem instanceof UIComponent uiComponent) {
                    float startY = getHeight() - firstItem.getItemHeight();
                    uiComponent.setPosition(
                        0f,
                        startY + sliderPathwayPos * (maxSize.y - getHeight())
                    );
                }
            }else {
                UIListItem lastItem = itemList.get(i - 1);
                if (item instanceof UIComponent uiComponent) {
                    if (lastItem instanceof UIComponent uiComponent2) {
                        uiComponent.setPosition(0, uiComponent2.getY() - lastItem.getItemHeight() - 1f);
                    }
                }
            }
        }
    }

    /**
     * 绘制列表
     * <p>
     * 项目从上往下一个个绘制，超出列表渲染区域的项目不绘制
     * */
    @Override
    public void draw (Batch batch, UIPanel parent) {
        //获取可以重复利用的矩形
        PoolableRectangle listRect = Pools.RECT.obtain();
        listRect.set(getX(), getY(), getWidth(), getHeight());
        PoolableRectangle itemRect = Pools.RECT.obtain();

        //绘制所有的项目
        float itemRenderX = getX();
        float itemRenderY = getY() + getHeight();
        //启用裁剪
        //超过列表大小的部分不绘制
        ScissorUtil.beginScissor(
            batch, GUICamera.INSTANCE.getCamera(),
            getAbsX(), getAbsY(), getWidth(), getHeight()
        );
        for (UIListItem listItem : this.getItems()) {
            if (listItem instanceof UIComponent uiComponent) {
                itemRenderX = uiComponent.getAbsX();
                itemRenderY = uiComponent.getAbsY();
            }

            listItem.draw(batch, this, itemRenderX, itemRenderY);
        }

        //用完回收
        Pools.RECT.free(listRect);
        Pools.RECT.free(itemRect);

        //绘制滚动条
        this.getScrollbar().draw(batch, this);
        //关掉裁剪
        ScissorUtil.endScissor(batch);
    }

    /**
     * 设置大小的同时设置交互区域网格大小，以及一直让滚动条贴在右边
     * <p>
     * 滚动进度会按比例保留，不会因调整大小而丢失
     * */
    @Override
    public UIComponent setSize (float width, float height) {
        getInteractGridSize().set((int) width, (int) height);
        UIScrollBar scrollBar = this.getScrollbar();
        //记录调整前的滚动进度（0~1）
        float oldProgress = scrollBar.getSliderPathwayPos();
        scrollBar
            .setHeight((int) height)
            .setPosition(width - scrollBar.getWidth(), 0f);
        //按旧进度恢复滑块位置，避免滚动进度丢失
        switch (scrollBar.getType()) {
            case VERTICAL: {
                float maxY = height - scrollBar.getSliderHeight();
                scrollBar.setSliderY(maxY - oldProgress * maxY);
                break;
            }
            case HORIZONTAL: {
                float maxX = width - scrollBar.getSliderWidth();
                scrollBar.setSliderX(oldProgress * maxX);
                break;
            }
        }

        return super.setSize(width, height);
    }

    public List<UIListItem> getItems () {
        return this.items;
    }

    public UIList setItems (List<UIListItem> items) {
        this.items = items;
        return this;
    }

    public UIScrollBar getScrollbar () {
        return this.scrollbar;
    }

    public UIList setScrollbar (UIScrollBar scrollbar) {
        this.scrollbar = scrollbar;
        return this;
    }
}
