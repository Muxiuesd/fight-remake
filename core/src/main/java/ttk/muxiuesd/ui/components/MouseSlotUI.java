package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 鼠标物品槽UI
 * <p>
 * 鼠标上持有的物品
 * */
public class MouseSlotUI extends PlayerSlotUI {
    //单例模式
    private static MouseSlotUI INSTANCE;

    public static MouseSlotUI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MouseSlotUI();
        }
        return INSTANCE;
    }

    public static void setInstance(MouseSlotUI instance) {
        if (instance != null) INSTANCE = instance;
    }

    private ItemStack itemStack = ItemStack.VOID;
    //public UIPanel curPanel;
    public UIScreen curScreen; //当前鼠标物品槽UI所属的UIScreen

    private MouseSlotUI () {
        super(10000f, 10000f, SLOT_WIDTH, SLOT_HEIGHT);
        setEnabled(false);
        setZIndex(10000);
    }

    /**
     * 激活鼠标物品槽
     * @param screen 在哪个UIScreen上激活物品槽
     * */
    public static MouseSlotUI activate (UIScreen screen) {
        MouseSlotUI instance = getInstance();
        instance.setPosition(Util.getMouseUIPosition());
        instance.curScreen = screen;
        screen.addComponent(instance);

        return INSTANCE;
    }

    /**
     * 使鼠标物品槽失活
     * */
    public static MouseSlotUI deactivate () {
        MouseSlotUI instance = getInstance();
        if (instance.curScreen != null) {
            instance.curScreen.removeComponent(instance);
            instance.curScreen = null;
        }
        instance.clearItem();
        return instance;
    }

    /**
     * 如果鼠标槽位在指定的屏幕上激活且持有物品，就把物品丢出来并清空槽位
     * @param screen 要检查的UI屏幕
     * @param player 丢弃物品的玩家
     * @return 是否丢弃了物品
     * */
    public static boolean dropItemIfActiveOn (UIScreen screen, Player player) {
        MouseSlotUI instance = getInstance();
        if (instance.isActive() && instance.curScreen == screen && !instance.isNullSlot()) {
            ItemStack itemStack = instance.getItemStack();
            instance.clearItem();
            player.dropItem(itemStack);
            return true;
        }
        return false;
    }

    /**
     * 如果鼠标槽位在指定的屏幕上激活且持有物品，就从槽位里分离出1个物品丢出来
     * <p>
     * 槽位里只剩1个物品时，丢出后槽位会被清空
     * @param screen 要检查的UI屏幕
     * @param player 丢弃物品的玩家
     * @return 是否丢弃了物品
     * */
    public static boolean dropOneIfActiveOn (UIScreen screen, Player player) {
        MouseSlotUI instance = getInstance();
        if (instance.isActive() && instance.curScreen == screen && !instance.isNullSlot()) {
            ItemStack itemStack = instance.getItemStack();
            ItemStack one = itemStack.split(1);
            player.dropItem(one);
            if (itemStack.getAmount() <= 0) {
                instance.clearItem();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isNullSlot () {
        return this.getItemStack().isVoid();
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public void setItemStack (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void clearItem () {
        this.itemStack = ItemStack.VOID;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (this.isNullSlot()) return;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        ItemStack stack = this.getItemStack();
        Item item = stack.getItem();
        float renderX = mouseUIPosition.x - getWidth() / 2;
        float renderY = mouseUIPosition.y - getHeight() / 2;

        ItemRenderer<Item> renderer = ItemRendererRegistry.get(item);
        //渲染器未注册（可能返回null）时不绘制该物品，避免崩溃
        if (renderer != null) {
            ItemRenderer.Context context = renderer.getContext(
                renderX, renderY,
                getWidth(), getHeight()
            );
            renderer.draw(batch, context, stack);
            renderer.freeContext(context);
        }

        int amount = stack.getAmount();
        if (amount > 1) drawAmount(batch, parent, (int) renderX, (int) renderY, amount);
    }

    /**
     * 检查这个鼠标物品槽位UI是否活跃，也就是检查它是否有在uiScreen上
     * */
    public boolean isActive () {
        return this.curScreen != null;
    }
}
