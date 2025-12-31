package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 鼠标物品槽UI
 * */
public class MouseSlotUI extends SlotUI {
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

    private ItemStack itemStack;
    //public UIPanel curPanel;
    public UIScreen curScreen; //当前鼠标物品槽UI所属的UIScreen

    private MouseSlotUI() {
        super(10000f, 10000f, SLOT_WIDTH, SLOT_HEIGHT, new GridPoint2(16, 16));
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
        return instance;
    }

    @Override
    public boolean isNullSlot () {
        return this.getItemStack() == null;
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
        this.itemStack = null;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (this.isNullSlot()) return;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        ItemStack stack = this.getItemStack();
        Item item = stack.getItem();
        float renderX = mouseUIPosition.x - getWidth() / 2;
        float renderY = mouseUIPosition.y - getHeight() / 2;
        batch.draw(item.textureRegion, renderX, renderY, getWidth(), getHeight());

        int amount = stack.getAmount();
        drawAmount(batch, parent, renderX, renderY, amount);
    }
}
