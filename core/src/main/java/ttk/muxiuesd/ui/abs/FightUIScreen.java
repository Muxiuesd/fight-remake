package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.system.game.InputHandleSystem;
import ttk.muxiuesd.ui.components.TooltipUI;

/**
 * Fight实现的UIScreen，fight游戏里面的UIScreen都直接继承这个
 * */
public abstract class FightUIScreen extends UIScreen {

    @Override
    public void show () {
        //调整大小
        OrthographicCamera camera = GUICamera.INSTANCE.getCamera();
        resize(camera.viewportWidth, camera.viewportHeight);

        InputHandleSystem.getInstance().addProcessor(this);
    }

    @Override
    public void hide () {
        super.hide();

        //如果有TooltipUI就让它失活
        getComponents().forEach((uiComponent -> {
            if (uiComponent instanceof TooltipUI) TooltipUI.deactivate();
        }));

        handleDelayEvents();
        InputHandleSystem.getInstance().removeProcessor(this);
    }
}
