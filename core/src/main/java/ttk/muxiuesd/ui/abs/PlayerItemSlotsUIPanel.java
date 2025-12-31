package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.TooltipUI;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.util.pool.PoolableRectangle;

/**
 * 含有玩家物品的slotUI的面板，抽象类
 * */
public abstract class PlayerItemSlotsUIPanel extends UIPanel {
    private PlayerSystem playerSystem;

    public PlayerItemSlotsUIPanel (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;
    }
    public PlayerItemSlotsUIPanel (PlayerSystem playerSystem,
                                   float x, float y,
                                   float width, float height,
                                   GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.playerSystem = playerSystem;
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (getComponents().isEmpty()) return;

        PoolableRectangle rectangle = Pools.RECT.obtain();

        UIComponent mouseOverComponent = null;
        for (UIComponent component : getComponents()) {
            //跳过不可接触的UI
            if (!component.isEnabled()) continue;
            if (component instanceof SlotUI slotUI) {
                slotUI.setMouseOver(false);
            }
            rectangle.set(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            //鼠标在组件上面
            if (rectangle.contains(interactPos.x, interactPos.y)){
                mouseOverComponent = component;
                //计算交互区域坐标
                GridPoint2 interactGrid = component.getInteractGridSize();
                Vector2 position = component.getPosition();
                Vector2 size = component.getSize();
                int xn = (int) ((interactPos.x - position.x) / size.x * interactGrid.x);
                int yn = (int) ((interactPos.y - position.y) / size.y * interactGrid.y);
                GridPoint2 grid = new GridPoint2(xn, yn);
                component.mouseOver(grid);
            }
        }
        //工具词条ui标记，默认为true说明鼠标没有指向任何一个物品槽位
        boolean flag = true;
        //当鼠标放在物品槽位上
        if (mouseOverComponent instanceof SlotUI slotUI) {
            slotUI.setMouseOver(true);
            //如果物品槽位上有物品，就激活物品的工具词条UI
            if (!slotUI.isNullSlot()) {
                TooltipUI.activate(this).setCurItemStack(slotUI.getItemStack());
                flag = false;
            }
        }
        if (flag) {
            TooltipUI.deactivate();
        }
        Pools.RECT.free(rectangle);
    }

    public PlayerSystem getPlayerSystem () {
        return this.playerSystem;
    }

    public PlayerItemSlotsUIPanel setPlayerSystem (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;
        return this;
    }
}
