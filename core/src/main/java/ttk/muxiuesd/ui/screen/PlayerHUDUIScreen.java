package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.HotbarUIPanel;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.ui.components.HotbarSlotUI;

/**
 * 玩家的HUD屏幕
 * <p>
 * 显示玩家的快捷栏、血条等UI组件
 * */
public class PlayerHUDUIScreen extends UIScreen {
    private PlayerSystem playerSystem;

    public PlayerHUDUIScreen (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        OrthographicCamera camera = GUICamera.INSTANCE.getCamera();
        float viewportWidth = camera.viewportWidth;
        float viewportHeight = camera.viewportHeight;
        float width = HotbarSlotUI.HOTBAR_WIDTH * 9;
        HotbarUIPanel hotbarUIPanel = new HotbarUIPanel(playerSystem,
            - width / 2, - viewportHeight / 2,
            width, HotbarSlotUI.HOTBAR_HEIGHT,
            new GridPoint2((int) width, (int) HotbarSlotUI.HOTBAR_HEIGHT));

        addComponent(hotbarUIPanel);
    }
}
