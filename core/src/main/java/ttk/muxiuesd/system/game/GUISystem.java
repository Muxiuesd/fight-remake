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

    private UIScreen curScreen; //当前渲染的ui屏幕


    @Override
    public void update (float delta) {
        if (this.screenNotNull()) {
            this.getCurScreen().update(delta);
        }
    }

    @Override
    public void draw (Batch batch) {
        if (this.screenNotNull()) this.getCurScreen().draw(batch);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.screenNotNull()) this.getCurScreen().renderShape(batch);
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
        if (this.screenNotNull()) this.getCurScreen().resize(viewportWidth, viewportHeight);
    }

    /**
     * 鼠标指针是否在UI组件上
     * */
    public boolean mouseOverUI () {
        return this.screenNotNull() && this.getCurScreen().isMouseOver();
    }

    public void setCurScreen (UIScreen curScreen) {
        if (this.screenNotNull()) {
            this.getCurScreen().hide();
        }
        this.curScreen = curScreen;
        curScreen.show();
    }

    public UIScreen getCurScreen () {
        return this.curScreen;
    }

    public boolean screenNotNull () {
        return this.curScreen != null;
    }

    @Override
    public int getRenderPriority () {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public void dispose () {
        if (this.getCurScreen() != null) this.setCurScreen(null);
    }
}
