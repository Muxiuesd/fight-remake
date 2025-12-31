package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 创造物品槽位UI
 * */
public class CreateSlotUI extends UIComponent {
    private TextureRegion slotHighlight;

    private ItemGroup itemGroup;
    private int index = 0;

    public CreateSlotUI (float x, float y) {
        super(x, y, SlotUI.SLOT_WIDTH, SlotUI.SLOT_HEIGHT, new GridPoint2(16, 16));

        this.slotHighlight = Util.loadTextureRegion(
            Fight.ID("slot_highlight"),
            Fight.UITexturePath("slot_highlight.png")
        );
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float renderX = getX();
        float renderY = getY();
        if (parent != null) {
            renderX += parent.getX();
            renderY += parent.getY();
        }

        //空物品不绘制
        if (isVisible() && !this.isNullSlot()){
            ItemStack itemStack = this.getItemGroup().get(this.getIndex());
            batch.draw(itemStack.getItem().textureRegion, renderX, renderY, getWidth(), getHeight());
        }
        //绘制鼠标放在槽位上的高光
        if (this.slotHighlight != null && isMouseOver()) {
            batch.draw(this.slotHighlight, renderX - 1, renderY - 1);
        }
    }

    public boolean isNullSlot () {
        return this.getIndex() >= this.getItemGroup().getItemsList().size();
    }

    public ItemGroup getItemGroup () {
        return this.itemGroup;
    }

    public CreateSlotUI setItemGroup (ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public int getIndex () {
        return this.index;
    }

    public CreateSlotUI setIndex (int index) {
        if (index >= 0) this.index = index;
        return this;
    }
}
