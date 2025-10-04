package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.text.TextUI;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 最基础的槽位UI组件
 * <p>
 * 可以与鼠标交互
 * */
public class SlotUI extends UIComponent {
    public static final float SLOT_WIDTH = 16f;
    public static final float SLOT_HEIGHT = 16f;


    private PlayerSystem playerSystem;
    private int index;  ///指向的玩家的某个容器的索引

    private TextUI textUI;

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

        this.textUI = new TextUI();
        this.textUI.setPosition(x, y);
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

        int amount = itemStack.getAmount();
        this.drawAmount(batch, parent, renderX, renderY, amount);
    }

    /**
     * 绘制数量字体
     * */
    public void drawAmount (Batch batch, UIPanel parent, float renderX, float renderY, int amount) {
        if (amount > 1) {
            this.textUI.setPosition(renderX, renderY);
            this.textUI.setText(String.valueOf(amount)).draw(batch, parent);
        }
    }

    /**
     * 核心算法
     * */
    @Override
    public boolean click (GridPoint2 interactPos) {
        MouseSlotUI mouseSlotUI = MouseSlotUI.getInstance();
        if (!this.isNullSlot()) {
            //点击这个有物品的槽位就把物品转移到鼠标物品槽里面，如果鼠标物品槽有东西就交换两者
            if (mouseSlotUI.isNullSlot()) {
                //激活鼠标物品槽
                MouseSlotUI.activate().setItemStack(this.getItemStack());
                this.clearItem();
            }else {
                //如果鼠标物品槽的物品通过类型检测，就交换物品
                if (this.checkItemType(mouseSlotUI.getItemStack())) {
                    ItemStack mouseItem = mouseSlotUI.getItemStack();
                    ItemStack slotItem = this.getItemStack();
                    mouseSlotUI.setItemStack(slotItem);
                    this.setItemStack(mouseItem);
                }
            }
        }else if (! mouseSlotUI.isNullSlot()) {
            //物品槽是空的，同时鼠标物品槽有物品，且物品类型检查通过，就把物品放进来
            if (this.checkItemType(mouseSlotUI.getItemStack())) {
                this.setItemStack(mouseSlotUI.getItemStack());
                MouseSlotUI.deactivate();
                mouseSlotUI.clearItem();
            }
        }
        return super.click(interactPos);
    }

    /**
     * 物品类型检测：检查要放进来的物品是否符合可放进来的类型
     * */
    public boolean checkItemType (ItemStack itemStack) {
        //默认不检查都可以放
        return true;
    }


    /**
     * 检查这个物品槽是否有物品
     * */
    public boolean isNullSlot () {
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return true;
        ItemStack itemStack = this.getInventory().getItemStack(this.getIndex());
        return itemStack == null;
    }

    public ItemStack getItemStack () {
        return this.getInventory().getItemStack(this.getIndex());
    }

    public void setItemStack (ItemStack itemStack) {
        this.getInventory().setItemStack(this.getIndex(), itemStack);
    }

    /**
     * 清除槽位上对应的物品
     * */
    public void clearItem () {
        if (this.isNullSlot()) return;
        this.getInventory().clear(this.getIndex());
    }

    /**
     * 获取这个slot对应的容器
     * */
    public Inventory getInventory () {
        Player player = this.getPlayerSystem().getPlayer();
        return player.getBackpack();
    }

    public PlayerSystem getPlayerSystem() {
        return this.playerSystem;
    }

    public int getIndex() {
        return this.index;
    }
}
