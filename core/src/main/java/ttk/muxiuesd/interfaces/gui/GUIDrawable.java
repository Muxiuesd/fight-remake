package ttk.muxiuesd.interfaces.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.ui.components.UIPanel;

/**
 * GUI的绘制接口
 * */
public interface GUIDrawable {
    /**
     * @param parent 组件的父节点，是{@link UIPanel}
     * */
    void draw(Batch batch, UIPanel parent);
}
