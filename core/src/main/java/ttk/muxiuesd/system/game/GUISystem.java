package ttk.muxiuesd.system.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IGUIRender;
import ttk.muxiuesd.system.abs.GameSystem;
import ttk.muxiuesd.ui.abs.UIPanel;

/**
 * 游戏的GUI系统
 * */
public class GUISystem extends GameSystem implements IGUIRender {
    private UIPanel curPanel; //当前渲染的ui面板
    //也是单例模式
    private GUISystem() {}
    private static GUISystem INSTANCE;

    public static GUISystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GUISystem();
        }
        return INSTANCE;
    }

    @Override
    public void update (float delta) {
        if (this.curPanel != null) {
            curPanel.update(delta);
        }
    }

    @Override
    public void draw (Batch batch) {
        if (this.curPanel != null) curPanel.draw(batch);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.curPanel != null) curPanel.renderShape(batch);
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
        this.renderShape(shapeRenderer);
    }

    /**
     * 相机视口大小变动时调用
     * */
    public void resize (float viewportWidth, float viewportHeight) {
        if (this.curPanel != null) curPanel.resize(viewportWidth, viewportHeight);
    }

    /**
     * 鼠标指针是否在UI组件上
     * */
    public boolean mouseOverUI () {
        return this.curPanel != null && this.curPanel.isMouseOver();
    }

    public void setCurPanel (UIPanel curPanel) {
        this.curPanel = curPanel;
    }

    public UIPanel getCurPanel() {
        return this.curPanel;
    }

    @Override
    public int getRenderPriority () {
        return 10000;
    }

    @Override
    public void dispose () {
        if (this.curPanel != null) this.curPanel = null;
    }
}
