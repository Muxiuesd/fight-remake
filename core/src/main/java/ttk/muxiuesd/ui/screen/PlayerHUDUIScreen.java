package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerHotbarUIPanel;
import ttk.muxiuesd.ui.abs.FightUIScreen;
import ttk.muxiuesd.ui.components.HotbarPlayerSlotUI;
import ttk.muxiuesd.ui.panel.InfoPanel;

/**
 * 玩家的HUD屏幕
 * <p>
 * 显示玩家的快捷栏、血条等UI组件
 * */
public class PlayerHUDUIScreen extends FightUIScreen {
    private PlayerSystem playerSystem;
    private PlayerHotbarUIPanel playerHotbarUIPanel;


    public PlayerHUDUIScreen (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        OrthographicCamera camera = GUICamera.INSTANCE.getCamera();
        float viewportHeight = camera.viewportHeight;
        float width = HotbarPlayerSlotUI.HOTBAR_WIDTH * 9;

        this.playerHotbarUIPanel = new PlayerHotbarUIPanel(playerSystem, - width / 2, - viewportHeight / 2);
        addComponent(this.playerHotbarUIPanel.auto());


        //信息面板（仿 Minecraft F3），默认停靠屏幕左上角，按 I 键开关
        InfoPanel.getInstance().resize(camera.viewportWidth, camera.viewportHeight);
        addComponent(InfoPanel.getInstance());

        //addComponent(this.playerHealthBar);
    }
}
