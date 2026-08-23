package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.ui.text.TextUI;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 最基础的物品槽位UI组件
 * <p>
 * 与鼠标交互
 * */
public class SlotUI extends UIComponent {
    public static final float SLOT_WIDTH = 18f;
    public static final float SLOT_HEIGHT = 18f;
    public static final float SLOT_UI_WIDTH = SLOT_WIDTH;
    public static final float SLOT_UI_HEIGHT = SLOT_HEIGHT;
    public static final float ITEM_RENDER_WIDTH = SLOT_WIDTH - 2f;
    public static final float ITEM_RENDER_HEIGHT = SLOT_HEIGHT - 2f;


    private Resource<TextureRegion> slotHighlightResource;
    private TextUI ammountTextUI;       //显示数量的文本UI

    public SlotUI (float x, float y, float width, float height) {
        super(x, y, width, height, new GridPoint2(1, 1));
        this.slotHighlightResource = Resource.ofTextureRegion(
            Fight.ID("slot_highlight"),
            Fight.UITexturePath("slot_highlight.png")
        );
        this.ammountTextUI = new TextUI();
        this.ammountTextUI.setPosition(x, y);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (!this.getItemStack().isVoid()) TooltipUI.activate(getScreen(), this);
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
            //batch.draw(itemStack.getItem().getTextureRegion(), renderX, renderY, getWidth(), getHeight());
            ItemRenderer<Item> renderer = ItemRendererRegistry.get(itemStack);
            //渲染器未注册（可能返回null）时不绘制该物品，避免崩溃
            if (renderer != null) {
                ItemRenderer.Context context = renderer.getContext(
                    renderX + 1, renderY + 1,
                    ITEM_RENDER_WIDTH, ITEM_RENDER_HEIGHT
                );
                renderer.draw(batch, context, itemStack);
                renderer.freeContext(context);
            }

            //数量大于1才绘制数量的字体文本
            int amount = itemStack.getAmount();
            if (amount > 1) {
                this.drawAmount(batch, parent, renderX, renderY, amount);
            }
        }

        //绘制鼠标放在槽位上的高光
        if (isMouseOver()) {
            batch.draw(this.getSlotHighlightTextureRegion(), renderX, renderY);
        }
    }

    /**
     * 绘制数量字体，会绘制在槽位UI的右下角
     * */
    public void drawAmount (Batch batch, UIPanel parent, float renderX, float renderY, int amount) {
        this.ammountTextUI
            .setPosition(renderX + getWidth() - this.ammountTextUI.getRenderWidth(), renderY + 1f)
            .setText(String.valueOf(amount))
            .draw(batch, parent);
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
     * 获取这个物品槽位对应的物品（默认空堆叠）
     * */
    public ItemStack getItemStack () {
        return ItemStack.VOID;
    }

    @Override
    public SlotUI setPosition (float x, float y) {
        super.setPosition(x, y);
        return this;
    }

    @Override
    public SlotUI setPosition (Vector2 pos) {
        super.setPosition(pos);
        return this;
    }

    /**
     * 获取槽位高光贴图
     * */
    public TextureRegion getSlotHighlightTextureRegion () {
        return this.getSlotHighlightResource().get();
    }

    public Resource<TextureRegion> getSlotHighlightResource () {
        return this.slotHighlightResource;
    }

    public SlotUI setSlotHighlightResource (Resource<TextureRegion> slotHighlightResource) {
        this.slotHighlightResource = slotHighlightResource;
        return this;
    }
}
