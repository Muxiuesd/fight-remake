package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import game.muxiuesd.bedrockcore.app.ui.components.UIScrollBar;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.ItemGroups;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.PlayerItemSlotsUIPanel;
import ttk.muxiuesd.ui.components.CreateSlotUI;
import ttk.muxiuesd.ui.components.PlayerSlotUI;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.ui.components.TabButtonUI;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 玩家创造背包面板
 * */
public class PlayerCreateTabUIPanel extends PlayerItemSlotsUIPanel {
    public static final int TAB_BACKGROUND_WIDTH = 195;
    public static final int TAB_BACKGROUND_HEIGHT = 136;

    private Resource<TextureRegion> tabBackgroundTextureRegionResource;
    private UIPanel tabAboveButtonsUIPanel;     //上面的物品组页面按钮
    private Array<TabButtonUI> tabButtons;
    private Array<CreateSlotUI> createSlots;

    private TabButtonUI tabLeftButtonUI;
    private TabButtonUI tabMiddleButtonUI1;
    private TabButtonUI tabMiddleButtonUI2;
    private TabButtonUI tabMiddleButtonUI3;
    private TabButtonUI tabRightButtonUI;

    private ItemGroup curItemGroup;
    private UIScrollBar scrollBar;
    private int firstCreateSlotIndex = 0;
    private int firstTabButtonIndex = 0;    //上下两个位置各有五个物品组页面按钮，三个中间，两边各一个


    public PlayerCreateTabUIPanel (PlayerSystem playerSystem) {
        super(playerSystem);
        setSize(TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT + TabButtonUI.BUTTON_HEIGHT - 4);
        setPosition(- getWidth()/ 2f,  - getHeight() / 2f);
        autoInteractGridSize();

        this.tabBackgroundTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab"),
            Fight.UITexturePath("tab/tab_items.png")
        );

        this.tabAboveButtonsUIPanel = new UIPanel();
        //物品组页面按钮UI面板的位置在物品页面的上面
        this.tabAboveButtonsUIPanel
            .setPosition(0, TAB_BACKGROUND_HEIGHT - 4f)
            .setSize(TAB_BACKGROUND_WIDTH, TabButtonUI.BUTTON_HEIGHT);
        this.tabAboveButtonsUIPanel.autoInteractGridSize();
        addComponent(this.tabAboveButtonsUIPanel);

        this.initTabButtons();
        this.initSlots();

        //默认选中最左边的
        this.setSelectedTabButton(this.tabLeftButtonUI);

        this.scrollBar = new UIScrollBar();
        this.scrollBar
            .setSliderWidth(12f)
            .setSliderHeight(15f)
            .sliderGotoStart()
            .setSliderWayVisible(false)
            .setPosition(175f, 8f);

        addComponent(this.scrollBar);
    }

    /**
     * 初始化所有物品组页面按钮
     * */
    private void initTabButtons () {
        this.tabButtons = new Array<>();
        this.tabLeftButtonUI = new TabButtonUI(this,
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left"),
                Fight.UITexturePath("tab/tab_above_left.png")
            ),
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left_selected"),
                Fight.UITexturePath("tab/tab_above_left_selected.png")
            ));

        //中间的按钮的贴图是公用的
        Resource<TextureRegion> tabAboveMiddleResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle"),
            Fight.UITexturePath("tab/tab_above_middle.png")
        );
        Resource<TextureRegion> tabAboveMiddleSelectedResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle_selected"),
            Fight.UITexturePath("tab/tab_above_middle_selected.png")
        );
        this.tabMiddleButtonUI1 = new TabButtonUI(this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabMiddleButtonUI2 = new TabButtonUI(this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabMiddleButtonUI3 = new TabButtonUI(this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabRightButtonUI = new TabButtonUI(this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);

        this.tabLeftButtonUI.setPosition(0f, 0f);
        this.tabMiddleButtonUI1.setPosition(
            this.tabLeftButtonUI.getWidth() + 1f,
            0f
        );
        this.tabMiddleButtonUI2.setPosition(
            this.tabMiddleButtonUI1.getX() + this.tabMiddleButtonUI1.getWidth() + 1f,
            0f
        );
        this.tabMiddleButtonUI3.setPosition(
            this.tabMiddleButtonUI2.getX() + this.tabMiddleButtonUI2.getWidth() + 1f,
            0f
        );
        this.tabRightButtonUI.setPosition(
            this.tabMiddleButtonUI3.getX() + this.tabMiddleButtonUI3.getWidth() + 1f,
            0f
        );

        //把这些页面按钮加进去
        this.addTabButton(this.tabLeftButtonUI);
        this.addTabButton(this.tabMiddleButtonUI1);
        this.addTabButton(this.tabMiddleButtonUI2);
        this.addTabButton(this.tabMiddleButtonUI3);
        this.addTabButton(this.tabRightButtonUI);

        //设置每一个物品组页面对应的物品组
        this.tabLeftButtonUI.setDisplayItemGroup(ItemGroups.NATURE_BLOCK_ITEM);
        this.tabMiddleButtonUI1.setDisplayItemGroup(ItemGroups.TOOL_BLOCK_ITEM);
        this.tabMiddleButtonUI2.setDisplayItemGroup(ItemGroups.EQUIPMENT_ITEM);
        this.tabMiddleButtonUI3.setDisplayItemGroup(ItemGroups.WEAPON_ITEM);
        this.tabRightButtonUI.setDisplayItemGroup(ItemGroups.MATERIAL_ITEM);
    }

    /**
     * 添加物品组页面按钮
     * */
    public void addTabButton (TabButtonUI tabButtonUI) {
        this.getTabAboveButtonsUIPanel().addComponent(tabButtonUI);
        this.getTabButtons().add(tabButtonUI);
    }

    private void initSlots () {
        this.createSlots = new Array<>();

        float trueHeight = SlotUI.SLOT_HEIGHT + 2;
        float trueWidth = SlotUI.SLOT_WIDTH + 2;
        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            addComponent(new PlayerSlotUI(getPlayerSystem(), index, 9 + (index * trueWidth), 8));
        }

        //创造槽位
        int slotIndex = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addCreateSlotUI(
                    new CreateSlotUI(9 + (x * trueWidth), 102 - y * trueHeight)
                        .setIndex(slotIndex)
                );
                slotIndex++;
            }
        }
    }

    /**
     * 添加创造物品槽位UI
     * */
    private void addCreateSlotUI (CreateSlotUI createSlotUI) {
        if (!this.createSlots.contains(createSlotUI, true)) {
            this.createSlots.add(createSlotUI);
            addComponent(createSlotUI);
        }
    }

    /**
     * 设置被选中的物品组页面
     * */
    public void setSelectedTabButton (TabButtonUI tabButtonUI) {
        this.getTabButtons().forEach(tabButtonUI1 -> tabButtonUI1.setSelected(false));
        tabButtonUI.setSelected(true);
        this.setTabItemGroup(tabButtonUI.getDisplayItemGroup());
    }

    /**
     * 设置当前面板要显示的物品组
     * */
    public void setTabItemGroup (ItemGroup itemGroup) {
        if (itemGroup == null) return;
        this.setCurItemGroup(itemGroup);
        this.createSlots.forEach((createSlotUI -> createSlotUI.setItemGroup(itemGroup)));
    }

    @Override
    public void update (float delta) {
        super.update(delta);

        //滚动条的滑块位置与创造槽位索引的对应
        UIScrollBar scroll = this.getScrollBar();
        float value = scroll.getSliderPathwayPos();
        ItemGroup itemGroup = this.getCurItemGroup();
        int count = itemGroup.getItemCount();
        float row = (count / 9f);
        this.firstCreateSlotIndex = (int) (value * row) * 9;

        int createSlotIndex = 0;
        for (int index = this.firstCreateSlotIndex; index < this.createSlots.size; index++) {
            this.createSlots.get(createSlotIndex).setIndex(index);
            createSlotIndex++;
        }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        //绘制背景贴图
        batch.draw(this.getTabBackground(), getX(), getY(), TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT);

        //绘制其他子组件
        super.draw(batch, parent);
    }

    public UIPanel getTabAboveButtonsUIPanel () {
        return this.tabAboveButtonsUIPanel;
    }

    public PlayerCreateTabUIPanel setTabAboveButtonsUIPanel (UIPanel tabAboveButtonsUIPanel) {
        this.tabAboveButtonsUIPanel = tabAboveButtonsUIPanel;
        return this;
    }

    public Array<TabButtonUI> getTabButtons () {
        return this.tabButtons;
    }

    public PlayerCreateTabUIPanel setTabButtons (Array<TabButtonUI> tabButtons) {
        this.tabButtons = tabButtons;
        return this;
    }

    public ItemGroup getCurItemGroup () {
        return this.curItemGroup;
    }

    public PlayerCreateTabUIPanel setCurItemGroup (ItemGroup itemGroup) {
        if (itemGroup != null) this.curItemGroup = itemGroup;
        return this;
    }

    public int getFirstCreateSlotIndex () {
        return this.firstCreateSlotIndex;
    }

    public PlayerCreateTabUIPanel setFirstCreateSlotIndex (int index) {
        this.firstCreateSlotIndex = Math.max(index, 0);
        return this;
    }

    public int getFirstTabButtonIndex () {
        return this.firstTabButtonIndex;
    }

    public PlayerCreateTabUIPanel setFirstTabButtonIndex (int firstTabButtonIndex) {
        this.firstTabButtonIndex = firstTabButtonIndex;
        return this;
    }

    public UIScrollBar getScrollBar () {
        return this.scrollBar;
    }

    public TextureRegion getTabBackground () {
        return this.getTabBackgroundTextureRegionResource().get();
    }

    public Resource<TextureRegion> getTabBackgroundTextureRegionResource () {
        return this.tabBackgroundTextureRegionResource;
    }
}
