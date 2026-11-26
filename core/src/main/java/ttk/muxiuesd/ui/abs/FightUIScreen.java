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

        //先把延迟添加的组件加入集合，避免刚激活（还在delayAdd队列）的组件漏掉失活
        handleDelayEvents();

        //如果有TooltipUI就让它失活
        getComponents().forEach((uiComponent -> {
            if (uiComponent instanceof TooltipUI) TooltipUI.deactivate();
        }));

        //再处理一次延迟删除，把刚失活的TooltipUI真正移出组件集合
        handleDelayEvents();

        InputHandleSystem.getInstance().removeProcessor(this);
    }
}
