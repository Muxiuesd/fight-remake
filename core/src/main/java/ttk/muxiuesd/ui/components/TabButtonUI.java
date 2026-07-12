package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 创造背包页面的左上页面按钮UI
 * */
public class TabButtonUI extends UIComponent {
    public static final float BUTTON_WIDTH = 28f, BUTTON_HEIGHT = 32f;
    public static final float ICON_WIDTH = 16f, ICON_HEIGHT = 16f;
    public static final Vec2 ICON_RENDER_OFFSET = new Vec2((BUTTON_WIDTH - ICON_WIDTH) / 2f, (BUTTON_HEIGHT - ICON_HEIGHT) / 2f);


    private Resource<TextureRegion> tabTextureRegionResource;           //没被选中状态的贴图
    private Resource<TextureRegion> tabSelectedTextureRegionResource;   //被选中状态的贴图
    private ItemGroup displayItemGroup; //当前按钮所对应的物品组
    private boolean isSelected;         //当前页面是否被选中


    public TabButtonUI (Resource<TextureRegion> tabTextureRegionResource,
                        Resource<TextureRegion> tabSelectedTextureRegionResource) {
        this.tabTextureRegionResource = tabTextureRegionResource;
        this.tabSelectedTextureRegionResource = tabSelectedTextureRegionResource;
        setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float renderX = getAbsX();
        float renderY = getAbsY();

        //绘制背景
        batch.draw(this.getDisplayTextureRegion(), renderX, renderY, getWidth(), getHeight());

        //绘制当前展示物品组的图标，使用物品自己的渲染器
        ItemStack iconItemStack = this.displayItemGroup.getIconItemStack();
        ItemRenderer<Item> renderer = ItemRendererRegistry.get(iconItemStack.getItem());
        ItemRenderer.Context rendererContext = renderer.getContext(
            renderX + ICON_RENDER_OFFSET.getX(), renderY + ICON_RENDER_OFFSET.getY(),
            ICON_WIDTH, ICON_HEIGHT
        );
        renderer.draw(batch, rendererContext, iconItemStack);
    }

    public ItemGroup getDisplayItemGroup () {
        return this.displayItemGroup;
    }

    public TabButtonUI setDisplayItemGroup (ItemGroup displayItemGroup) {
        this.displayItemGroup = displayItemGroup;
        return this;
    }

    public boolean isSelected () {
        return this.isSelected;
    }

    public TabButtonUI setSelected (boolean selected) {
        isSelected = selected;
        return this;
    }

    /**
     * 根据是否被选中的状态获取渲染的贴图
     * */
    public TextureRegion getDisplayTextureRegion () {
        if (!this.isSelected()) {
            return this.getTabTextureRegionResource().get();
        }
        return this.getTabSelectedTextureRegionResource().get();
    }

    public Resource<TextureRegion> getTabTextureRegionResource () {
        return this.tabTextureRegionResource;
    }

    public TabButtonUI setTabTextureRegionResource (Resource<TextureRegion> tabTextureRegionResource) {
        this.tabTextureRegionResource = tabTextureRegionResource;
        return this;
    }

    public Resource<TextureRegion> getTabSelectedTextureRegionResource () {
        return this.tabSelectedTextureRegionResource;
    }

    public TabButtonUI setTabSelectedTextureRegionResource (Resource<TextureRegion> tabSelectedTextureRegionResource) {
        this.tabSelectedTextureRegionResource = tabSelectedTextureRegionResource;
        return this;
    }
}
