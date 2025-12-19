package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.components.EquipmentSlotUI;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.TooltipUI;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.util.pool.PoolableRectangle;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

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
        //绘制背景贴图
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());

        //绘制玩家布娃娃
        Player player = this.playerSystem.getPlayer();
        EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(player.getID());
        EntityRenderer.Context context = renderer.getContext();
        context.x = getX() + 33f;
        context.y = getY() + 100f;
        context.width = 32f;
        context.height = 32f;
        renderer.draw(batch, player, context);
        renderer.freeContext(context);

        super.draw(batch, parent);
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
                slotUI.setMouseIsOver(false);
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
            slotUI.setMouseIsOver(true);
            //如果物品槽位上有物品，就激活物品的工具词条UI
            if (!slotUI.isNullSlot()) {
                TooltipUI.activate().setCurItemStack(slotUI.getItemStack());
                flag = false;
            }
        }
        if (flag) {
            TooltipUI.deactivate();
        }
        Pools.RECT.free(rectangle);
    }
}
