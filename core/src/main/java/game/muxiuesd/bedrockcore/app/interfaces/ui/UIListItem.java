package game.muxiuesd.bedrockcore.app.interfaces.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.ui.components.UIList;

/**
 * 列表UI组件的项目的接口，可以添加进列表的UI组件必须实现此接口
 * */
public interface UIListItem {
    /**
     * 获取列表项目的宽高
     * */
    default Vector2 getSize () {
        return new Vector2(this.getItemHeight(), this.getItemHeight());
    }

    void update (UIList uiList, float delta);
    /**
     * @param itemRenderX 渲染的横坐标，是绝对坐标
     * @param itemRenderY 渲染的纵坐标，是绝对坐标
     * */
    void draw (Batch batch, UIList uiList, float itemRenderX, float itemRenderY);
    void renderShape (ShapeRenderer batch, UIList uiList);
    float getItemWidth ();
    float getItemHeight ();
}
