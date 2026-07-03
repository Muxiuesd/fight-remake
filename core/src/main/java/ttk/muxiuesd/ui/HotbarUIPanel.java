package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.HotbarPlayerSlotUI;
import ttk.muxiuesd.ui.components.PlayerHealthBar;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Player;


/**
 * 快捷栏UI的面板
 * */
public class HotbarUIPanel extends UIPanel {
    private PlayerSystem playerSystem;
    private PlayerHealthBar playerHealthBar;    //玩家血条面板
    private final Array<HotbarPlayerSlotUI> hotbarUIComponents = new Array<>();
    private TextureRegion selectedHotbarTextureRegion;  //快捷栏选中框贴图



    public HotbarUIPanel (PlayerSystem playerSystem, float x, float y) {
        this(playerSystem, x, y, 0, 0, new GridPoint2(0, 0));
    }
    public HotbarUIPanel (PlayerSystem playerSystem,
                          float x, float y, float width, float height, GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.playerSystem = playerSystem;

        this.initHotbarSlots();

        this.playerHealthBar = new PlayerHealthBar(playerSystem,
            0,
            SlotUI.SLOT_UI_HEIGHT + 4
        );
        addComponent(this.playerHealthBar);
    }

    private void initHotbarSlots () {
        for (int i = 0; i < 9; i ++) {
            HotbarPlayerSlotUI hotbarSlotUI = new HotbarPlayerSlotUI(this.playerSystem, i, i * HotbarPlayerSlotUI.HOTBAR_WIDTH, 0);
            addComponent(hotbarSlotUI);
            this.hotbarUIComponents.add(hotbarSlotUI);
        }
        this.selectedHotbarTextureRegion = Util.loadTextureRegion(
            Fight.ID("selected_hotbar"),
            Fight.UITexturePath("selected_hotbar.png")
        );
    }

    @Override
    public void resize (float viewportWidth, float viewportHeight) {
        //让快捷栏整体贴着窗口下面
        setPosition(getX(), - viewportHeight / 2);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        super.draw(batch, parent);
        //绘制快捷栏槽位
        //this.hotbarUIComponents.forEach(hotbarSlotUI -> hotbarSlotUI.draw(batch, this));

        //绘制快捷栏选中框
        Player player = this.playerSystem.getPlayer();
        for (HotbarPlayerSlotUI hotbar : this.hotbarUIComponents) {
            if (player.getHandIndex() == hotbar.getIndex()) {
                batch.draw(this.selectedHotbarTextureRegion,
                    getX() + hotbar.getX() - 2,
                    getY()+ hotbar.getY() - 1,
                    HotbarPlayerSlotUI.SELECTED_HOTBAR_WIDTH,
                    HotbarPlayerSlotUI.SELECTED_HOTBAR_HEIGHT);
                break;
            }
        }


    }
}
