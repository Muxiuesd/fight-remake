package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.HotbarUIPanel;
import ttk.muxiuesd.ui.components.HotbarPlayerSlotUI;

/**
 * 玩家的HUD屏幕
 * <p>
 * 显示玩家的快捷栏、血条等UI组件
 * */
public class PlayerHUDUIScreen extends UIScreen {
    private PlayerSystem playerSystem;
    private HotbarUIPanel hotbarUIPanel;


    public PlayerHUDUIScreen (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        OrthographicCamera camera = GUICamera.INSTANCE.getCamera();
        float viewportHeight = camera.viewportHeight;
        float width = HotbarPlayerSlotUI.HOTBAR_WIDTH * 9;

        this.hotbarUIPanel = new HotbarUIPanel(playerSystem, - width / 2, - viewportHeight / 2);
        addComponent(this.hotbarUIPanel.auto());


        //addComponent(this.playerHealthBar);
    }
}
