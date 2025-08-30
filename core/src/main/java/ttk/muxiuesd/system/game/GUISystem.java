package ttk.muxiuesd.system.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.gui.GUIResize;
import ttk.muxiuesd.interfaces.render.IGUIRender;
import ttk.muxiuesd.system.abs.GameSystem;
import ttk.muxiuesd.ui.abs.UIScreen;

/**
 * 游戏的GUI系统
 * */
public class GUISystem extends GameSystem implements IGUIRender, GUIResize {
    //也是单例模式
    private GUISystem() {}
    private static GUISystem INSTANCE;

    public static GUISystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GUISystem();
        }
        return INSTANCE;
    }

    private UIScreen curScreen; //当前渲染的ui面板


    @Override
    public void update (float delta) {
        if (this.getCurScreen() != null) {
            this.getCurScreen().update(delta);
        }
    }

    @Override
    public void draw (Batch batch) {
        if (this.getCurScreen() != null) this.getCurScreen().draw(batch);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.getCurScreen() != null) this.getCurScreen().renderShape(batch);
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
        this.renderShape(shapeRenderer);
    }

    /**
     * 相机视口大小变动时调用
     * */
    @Override
    public void resize (float viewportWidth, float viewportHeight) {
        if (this.getCurScreen() != null) this.getCurScreen().resize(viewportWidth, viewportHeight);
    }

    /**
     * 鼠标指针是否在UI组件上
     * */
    public boolean mouseOverUI () {
        return this.getCurScreen() != null && this.getCurScreen().isMouseOver();
    }

    public void setCurScreen (UIScreen curScreen) {
        this.curScreen = curScreen;
    }

    public UIScreen getCurScreen () {
        return this.curScreen;
    }

    @Override
    public int getRenderPriority () {
        return Integer.MAX_VALUE;
    }

    @Override
    public void dispose () {
        if (this.getCurScreen() != null) this.setCurScreen(null);
    }
}
