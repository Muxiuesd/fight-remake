package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector3;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.ui.PlayerCreateTabUIPanel;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 创造背包页面的物品组页面按钮UI（没直接用UIButton）
 * */
public class TabButtonUI extends UIComponent {
    public static final float BUTTON_WIDTH = 28f, BUTTON_HEIGHT = 32f;
    public static final float ICON_WIDTH = 16f, ICON_HEIGHT = 16f;
    public static final Vec2 ICON_RENDER_OFFSET = new Vec2((BUTTON_WIDTH - ICON_WIDTH) / 2f, (BUTTON_HEIGHT - ICON_HEIGHT) / 2f);
    public static final float RENDER_DELTA_Y = 4f, ICON_RENDER_DELTA_Y = 7f, SELECTED_ICON_RENDER_DELTA_Y = 4f;

    public enum Type {
        ABOVE,  //上部
        BELOW;  //底部
    }

    private PlayerCreateTabUIPanel createTabUIPanel;
    private Resource<TextureRegion> tabTextureRegionResource;           //没被选中状态的贴图
    private Resource<TextureRegion> tabSelectedTextureRegionResource;   //被选中状态的贴图
    private ItemGroup displayItemGroup; //当前按钮所对应的物品组
    private boolean isSelected;         //当前页面是否被选中
    private Type type;

    public TabButtonUI (Type type, PlayerCreateTabUIPanel createTabUIPanel,
                        Resource<TextureRegion> tabTextureRegionResource,
                        Resource<TextureRegion> tabSelectedTextureRegionResource) {
        this.type = type;
        this.createTabUIPanel = createTabUIPanel;
        this.tabTextureRegionResource = tabTextureRegionResource;
        this.tabSelectedTextureRegionResource = tabSelectedTextureRegionResource;
        setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setInteractGridSize(new GridPoint2((int) BUTTON_WIDTH, (int) BUTTON_HEIGHT));
    }

    @Override
    public boolean click (GridPoint2 interactPos, int button) {
        //只有左键点击切换选中的物品组页面
        if (button != Input.Buttons.LEFT) return super.click(interactPos, button);
        //设置这个物品组页面按钮是被选中的
        this.createTabUIPanel.setSelectedTabButton(this);

        Vector3 pos = new Vector3(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0);
        SpatialAudioSystem.getInstance().playUIAudio(
            Sounds.ITEM_CLICK,
            () -> pos
        );
        return super.click(interactPos, button);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float renderX = getAbsX();
        float renderY = getAbsY();

        //绘制背景
        if (this.getType() == Type.ABOVE) {
            batch.draw(this.getDisplayTextureRegion(), renderX, renderY - RENDER_DELTA_Y, getWidth(), getHeight());
        }else {
            batch.draw(this.getDisplayTextureRegion(), renderX, renderY + RENDER_DELTA_Y, getWidth(), getHeight());
        }

        //绘制当前展示物品组的图标，使用物品自己的渲染器
        ItemStack iconItemStack = this.displayItemGroup.getIconItemStack();
        ItemRenderer<Item> renderer = ItemRendererRegistry.get(iconItemStack.getItem());
        //渲染器未注册（可能返回null）时不绘制图标，避免崩溃
        if (renderer == null) return;

        ItemRenderer.Context rendererContext = renderer.getContext(
            renderX + ICON_RENDER_OFFSET.getX(), renderY + ICON_RENDER_OFFSET.getY(),
            ICON_WIDTH, ICON_HEIGHT
        );
        //根据类型判断上下偏移
        if (this.getType() == Type.ABOVE) {
            rendererContext.y -= ICON_RENDER_DELTA_Y;
        }else {
            rendererContext.y += ICON_RENDER_DELTA_Y;
        }
        //如果被选中，图标的渲染位置要偏移
        if (this.isSelected()) {
            //上面的按钮的图标要向上偏移
            if (this.getType() == Type.ABOVE) rendererContext.y += SELECTED_ICON_RENDER_DELTA_Y;
            //下面的按钮的图标要向下偏移
            else rendererContext.y -= SELECTED_ICON_RENDER_DELTA_Y;
        }
        renderer.draw(batch, rendererContext, iconItemStack);
        //释放渲染上下文，防止池对象泄漏
        renderer.freeContext(rendererContext);
    }

    /**
     * 获取被显示的物品组
     * */
    public ItemGroup getDisplayItemGroup () {
        return this.displayItemGroup;
    }

    /**
     * 设置被显示的物品组
     * */
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

    public Type getType () {
        return this.type;
    }

    public TabButtonUI setType (Type type) {
        this.type = type;
        return this;
    }
}
