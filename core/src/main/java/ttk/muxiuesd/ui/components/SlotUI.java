package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 最基础的槽位UI组件
 * */
public class SlotUI extends UIComponent {
    public static final float SLOT_WIDTH = 16f;
    public static final float SLOT_HEIGHT = 16f;


    private PlayerSystem playerSystem;
    private int index;  ///指向的玩家背包容器的索引


    public SlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        this(playerSystem, index, x, y, SLOT_WIDTH, SLOT_HEIGHT,
            new GridPoint2((int) SLOT_WIDTH, (int) SLOT_HEIGHT));
    }
    public SlotUI (PlayerSystem playerSystem, int index,
                  float x, float y, float width, float height,
                  GridPoint2 interactGridSize) {
        this(x, y, width, height, interactGridSize);

        this.playerSystem = playerSystem;
        this.index = index;
    }
    public SlotUI (float x, float y, float width, float height, GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (!isVisible() || this.isNullSlot()) return;

        float renderX = getX();
        float renderY = getY();
        if (parent != null) {
            renderX += parent.getX();
            renderY += parent.getY();
        }
        ItemStack itemStack = this.getItemStack();
        batch.draw(itemStack.getItem().texture, renderX, renderY, getWidth(), getHeight());
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        if (!this.isNullSlot()) {
            //点击这个有物品的槽位就把物品转移到鼠标物品槽里面，如果鼠标物品槽有东西就交换两者
            if (MouseSlotUI.INSTANCE.isNullSlot()) {
                //激活鼠标物品槽
                MouseSlotUI.activate().setItemStack(this.getItemStack());
                this.clearItem();
            }else {
                //交换物品
                ItemStack mouseItem = MouseSlotUI.INSTANCE.getItemStack();
                ItemStack slotItem = this.getItemStack();
                MouseSlotUI.INSTANCE.setItemStack(slotItem);
                this.setItemStack(mouseItem);
            }
        }else if (!MouseSlotUI.INSTANCE.isNullSlot()) {
            //物品槽是空的，同时鼠标物品槽有物品，就把物品放进来
            this.setItemStack(MouseSlotUI.INSTANCE.getItemStack());
            MouseSlotUI.deactivate();
            MouseSlotUI.INSTANCE.clearItem();
        }
        return super.click(interactPos);
    }

    /**
     * 检查这个物品槽是否有物品
     * */
    public boolean isNullSlot () {
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return true;
        ItemStack itemStack = player.getBackpack().getItemStack(this.getIndex());
        return itemStack == null;
    }

    public ItemStack getItemStack () {
        Player player = getPlayerSystem().getPlayer();
        return player.getBackpack().getItemStack(this.getIndex());
    }

    public void setItemStack (ItemStack itemStack) {
        Player player = getPlayerSystem().getPlayer();
        player.getBackpack().setItemStack(this.getIndex(), itemStack);
    }

    /**
     * 清除槽位上对应的物品
     * */
    public void clearItem () {
        if (this.isNullSlot()) return;
        getPlayerSystem().getPlayer().getBackpack().clear(this.getIndex());
    }


    public PlayerSystem getPlayerSystem() {
        return this.playerSystem;
    }

    public int getIndex() {
        return this.index;
    }
}
