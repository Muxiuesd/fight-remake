package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.components.EquipmentSlotUI;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.TooltipUI;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.util.pool.PoolableRectangle;

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
        float trueHeight = SlotUI.SLOT_HEIGHT + 2;
        float trueWidth = SlotUI.SLOT_WIDTH + 2;
        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            addComponent(new SlotUI(this.playerSystem, index, 8 + (index * trueWidth), 8));
        }

        //背包内部槽位
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addComponent(new SlotUI(this.playerSystem, x + (y * 9) + 9,
                    8 + (x * trueWidth), 30 + y * trueHeight));
            }
        }

        //装备槽位
        for (int y = 0; y < 4; y++) {
            addComponent(new EquipmentSlotUI(this.playerSystem, y, 8, 142 - (y * trueHeight)));
        }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parent);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (getComponents().isEmpty()) return;

        PoolableRectangle rectangle = Pools.RECT.obtain();
        boolean flag = true;

        for (UIComponent component : getComponents()) {
            //当鼠标放在物品槽位上
            if (component instanceof SlotUI slotUI) {
                rectangle.set(slotUI.getX(), slotUI.getY(), slotUI.getWidth(), slotUI.getHeight());
                if (rectangle.contains(interactPos.x, interactPos.y)
                    && !slotUI.isNullSlot()) {
                    //如果物品槽位上有物品，就激活物品的工具词条UI
                    TooltipUI.activate().setCurItemStack(slotUI.getItemStack());
                    flag = false;
                    break;
                }
            }
        }

        if (flag) {
            TooltipUI.deactivate();
        }
        Pools.RECT.free(rectangle);
    }
}
