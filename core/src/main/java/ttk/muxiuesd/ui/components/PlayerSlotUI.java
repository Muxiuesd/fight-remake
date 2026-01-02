package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 跟玩家相关的槽位UI组件
 * <p>
 * 显示跟玩家有关的物品槽位UI
 * */
public class PlayerSlotUI extends SlotUI {
    private PlayerSystem playerSystem;
    private int index;  ///指向的玩家的某个容器的索引


    public PlayerSlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        this(playerSystem, index, x, y, SLOT_WIDTH, SLOT_HEIGHT);
    }
    public PlayerSlotUI (PlayerSystem playerSystem, int index,
                         float x, float y, float width, float height) {
        this(x, y, width, height);

        this.playerSystem = playerSystem;
        this.index = index;
    }
    public PlayerSlotUI (float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    /**
     * 点击交互的核心算法
     * */
    @Override
    public boolean click (GridPoint2 interactPos) {
        MouseSlotUI mouseSlotUI = MouseSlotUI.getInstance();
        if (!this.isNullSlot()) {
            ItemStack stack = this.getItemStack();
            int amount = stack.getAmount();
            //点击这个有物品的槽位就把物品转移到鼠标物品槽里面，如果鼠标物品槽有东西，不同类就交换两者，同类就合并
            if (mouseSlotUI.isNullSlot()) {
                //按下shift是半数拿取
                if (KeyBindings.PlayerShift.wasPressed() && amount >= 2) {
                    //如果为奇数，就鼠标拿取一半且多一个
                    MouseSlotUI.activate(getScreen()).setItemStack(stack.split((amount / 2) + amount % 2));
                }else {
                    //全部拿走
                    //激活鼠标物品槽
                    MouseSlotUI.activate(getScreen()).setItemStack(stack);
                    this.clearItem();
                }
            }else {
                //到这里鼠标物品槽位是有东西的
                ItemStack mouseStack = mouseSlotUI.getItemStack();
                //检查鼠标物品槽的物品类型是否符合槽位
                if (this.checkItemType(mouseStack)){
                    //检查是否为同类物品
                    if (mouseStack.equals(stack)) {
                        //是同类物品就合并
                        stack.merge(mouseStack);
                        //合并物品后鼠标物品数量归零就清除
                        if (mouseStack.getAmount() == 0) MouseSlotUI.deactivate().clearItem();
                    }else {
                        //不是同类物品就交换两者
                        this.setItemStack(mouseStack);
                        mouseSlotUI.setItemStack(stack);
                    }
                }
            }
        }else if (! mouseSlotUI.isNullSlot()) {
            //物品槽是空的，同时鼠标物品槽有物品，且物品类型检查通过，就把物品放进来
            if (checkItemType(mouseSlotUI.getItemStack())) {
                this.setItemStack(mouseSlotUI.getItemStack());
                MouseSlotUI.deactivate().clearItem();
            }
        }
        this.getPlayerSystem().getPlayer().getBackpack().clear();
        return super.click(interactPos);
    }

    /**
     * 检查这个物品槽是否有物品
     * */
    @Override
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
