package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIPanel;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Player;

/**
 * 玩家的面板
 * */
public class PlayerUIPanel extends UIPanel {
    private PlayerSystem playerSystem;
    private final Array<HotbarUIComponent> hotbarUIComponents = new Array<>();
    private TextureRegion selectedHotbarTextureRegion;

    public PlayerUIPanel (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        addComponent(new Button(0, 0,10, 10, new GridPoint2(10, 10)));

        this.initHotbar();
    }

    private void initHotbar () {
        OrthographicCamera camera = GUICamera.INSTANCE.getCamera();
        float viewportWidth = camera.viewportWidth;
        float viewportHeight = camera.viewportHeight;
        for (int i = 0; i < 9; i ++) {
            HotbarUIComponent hotbarUIComponent = new HotbarUIComponent(this.playerSystem, i,
                HotbarUIComponent.HOTBAR_WIDTH * (- 0.5f + (i - 4)) /*让快捷栏整体居中*/,
                - viewportHeight / 2  /*让快捷栏整体贴着窗口下面*/
            );
            addComponent(hotbarUIComponent);
            this.hotbarUIComponents.add(hotbarUIComponent);
        }

        this.selectedHotbarTextureRegion = Util.loadTextureRegion(
            Fight.getId("selected_hotbar"),
            Fight.UITexturePath("selected_hotbar.png")
        );
    }

    @Override
    public void draw (Batch batch) {
        super.draw(batch);
        //绘制快捷栏选中框
        Player player = this.playerSystem.getPlayer();
        for (HotbarUIComponent hotbar : this.hotbarUIComponents) {
            if (player.getHandIndex() == hotbar.getIndex()) {
                batch.draw(this.selectedHotbarTextureRegion,
                    hotbar.getX() - 2, hotbar.getY() - 1,
                    HotbarUIComponent.SELECTED_HOTBAR_WIDTH,
                    HotbarUIComponent.SELECTED_HOTBAR_HEIGHT);
                break;
            }
        }
    }
}
