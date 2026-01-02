package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.text.TextUI;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 最基础的物品槽位UI组件
 * <p>
 * 与鼠标交互
 * */
public class SlotUI extends UIComponent {
    public static final float SLOT_WIDTH = 16f;
    public static final float SLOT_HEIGHT = 16f;
    public static final float SLOT_UI_WIDTH = SLOT_WIDTH + 2f;
    public static final float SLOT_UI_HEIGHT = SLOT_HEIGHT + 2f;

    private TextureRegion slotHighlight;
    private TextUI textUI;

    public SlotUI (float x, float y, float width, float height) {
        super(x, y, width, height, new GridPoint2(1, 1));
        this.slotHighlight = Util.loadTextureRegion(
            Fight.ID("slot_highlight"),
            Fight.UITexturePath("slot_highlight.png")
        );
        this.textUI = new TextUI();
        this.textUI.setPosition(x, y);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (this.getItemStack() != null) TooltipUI.activate(getScreen(), this);
    }

    @Override
    public void mouseDown () {
        TooltipUI.deactivate();
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (!isVisible()) return;

        float renderX = getX();
        float renderY = getY();
        if (parent != null) {
            renderX += parent.getX();
            renderY += parent.getY();
        }

        //空物品槽位不绘制
        if (!this.isNullSlot()){
            ItemStack itemStack = this.getItemStack();
            batch.draw(itemStack.getItem().textureRegion, renderX, renderY, getWidth(), getHeight());

            //数量大于1才绘制
            int amount = itemStack.getAmount();
            if (amount > 1) this.drawAmount(batch, parent, renderX, renderY, amount);
        }
        //绘制鼠标放在槽位上的高光
        if (this.slotHighlight != null && isMouseOver()) {
            batch.draw(this.slotHighlight, renderX - 1, renderY - 1);
        }
    }

    /**
     * 绘制数量字体
     * */
    public void drawAmount (Batch batch, UIPanel parent, float renderX, float renderY, int amount) {
        this.textUI.setPosition(renderX, renderY);
        this.textUI.setText(String.valueOf(amount)).draw(batch, parent);
    }



    /**
     * 物品类型检测：检查要放进来的物品是否符合可放进来的类型
     * */
    public boolean checkItemType (ItemStack itemStack) {
        //默认不检查都可以放
        return true;
    }

    /**
     * 是否是空的物品槽，默认就是空的
     * */
    public boolean isNullSlot () {
        return true;
    }

    /**
     * 获取这个物品槽位对应的物品
     * */
    public ItemStack getItemStack () {
        return null;
    }
}
