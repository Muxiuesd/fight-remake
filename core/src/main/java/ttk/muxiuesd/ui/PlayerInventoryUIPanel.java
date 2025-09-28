package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.UIPanel;

/**
 * 玩家背包容器UI面板
 * */
public class PlayerInventoryUIPanel extends UIPanel {
    private PlayerSystem playerSystem;
    private TextureRegion background;

    public PlayerInventoryUIPanel(PlayerSystem playerSystem, TextureRegion background, float width, float height) {
        super(- width / 2, - height / 2, width, height,
            new GridPoint2(background.getRegionWidth(), background.getRegionHeight())
        );
        this.playerSystem = playerSystem;
        this.background = background;

        this.initSlots();
    }

    /**
     * 初始化所有物品槽位
     * */
    private void initSlots () {
        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            SlotUI slotUI = new SlotUI(this.playerSystem, index,
                8 + (index * (SlotUI.SLOT_WIDTH + 2)), 8);
            addComponent(slotUI);
        }

        //背包内部槽位
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                SlotUI slotUI = new SlotUI(this.playerSystem, x + y * 9 + 9,
                    8 + (x * (SlotUI.SLOT_WIDTH + 2)), 30 + y * (SlotUI.SLOT_HEIGHT + 2));
                addComponent(slotUI);
            }
        }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parent);

        BitmapFont font = Fonts.MC.getFont(10);
        font.draw(batch, "test", getX(), getY());
    }

}
