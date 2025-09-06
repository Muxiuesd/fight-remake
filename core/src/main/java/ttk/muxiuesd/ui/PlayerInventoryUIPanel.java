package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Player;

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

    private void initSlots () {
        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            SlotUI slotUI = new SlotUI(this.playerSystem, index,
                8 + (index * (SlotUI.SLOT_WIDTH + 2)), 8);
            addComponent(slotUI);
        }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());

        Player player = this.playerSystem.getPlayer();
        Backpack backpack = player.getBackpack();

        super.draw(batch, parent);
    }
}
