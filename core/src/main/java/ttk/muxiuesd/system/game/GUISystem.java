package ttk.muxiuesd.system.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IGUIRender;
import ttk.muxiuesd.system.abs.GameSystem;
import ttk.muxiuesd.ui.PlayerUIPanel;
import ttk.muxiuesd.ui.abs.UIPanel;

/**
 * 游戏的GUI系统
 * */
public class GUISystem extends GameSystem implements IGUIRender {
    private UIPanel curPanel; //当前渲染的ui面板

    public GUISystem() {
        this.setCurPanel(new PlayerUIPanel());
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
