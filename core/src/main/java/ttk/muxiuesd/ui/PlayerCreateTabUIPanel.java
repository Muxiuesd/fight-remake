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
    /// 创造物品背包面板贴图的渲染宽高
    public static final float TAB_BACKGROUND_WIDTH = 195f, TAB_BACKGROUND_HEIGHT = 136f;

    private Resource<TextureRegion> tabBackgroundTextureRegionResource;
    private UIPanel tabAboveButtonsUIPanel;     //上面的物品组页面按钮
    private UIPanel tabBelowButtonsUIPanel;     //下面的物品组页面按钮
    private Array<TabButtonUI> tabButtons;
    private Array<CreateSlotUI> createSlots;

    private TabButtonUI tabAboveLeftButtonUI;
    private TabButtonUI tabAboveMiddleButtonUI1;
    private TabButtonUI tabAboveMiddleButtonUI2;
    private TabButtonUI tabAboveMiddleButtonUI3;
    private TabButtonUI tabAboveRightButtonUI;
    private TabButtonUI tabBelowLeftButtonUI;
    private TabButtonUI tabBelowMiddleButtonUI1;
    private TabButtonUI tabBelowMiddleButtonUI2;
    private TabButtonUI tabBelowMiddleButtonUI3;
    private TabButtonUI tabBelowRightButtonUI;


    private ItemGroup curItemGroup;
    private UIScrollBar scrollBar;
    private int firstCreateSlotIndex = 0;   //创造物品槽位的起始索引
    private int firstTabButtonIndex = 0;    //上下两个位置各有五个物品组页面按钮，三个中间，两边各一个


    public PlayerCreateTabUIPanel (PlayerSystem playerSystem) {
        super(playerSystem);
        setSize(TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT + TabButtonUI.BUTTON_HEIGHT * 2f);
        setPosition(- getWidth()/ 2f,  - getHeight() / 2f);
        autoInteractGridSize();

        this.tabBackgroundTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab"),
            Fight.UITexturePath("tab/tab_items.png")
        );

        this.tabAboveButtonsUIPanel = new UIPanel();
        //物品组页面按钮UI面板的位置在物品页面的上面
        this.tabAboveButtonsUIPanel
            .setPosition(0, getHeight() - TabButtonUI.BUTTON_HEIGHT)
            .setSize(TAB_BACKGROUND_WIDTH, TabButtonUI.BUTTON_HEIGHT);
        this.tabAboveButtonsUIPanel.autoInteractGridSize();

        this.tabBelowButtonsUIPanel = new UIPanel();
        this.tabBelowButtonsUIPanel
            .setPosition(0f, 0f)
            .setSize(TAB_BACKGROUND_WIDTH, TabButtonUI.BUTTON_HEIGHT);
        this.tabBelowButtonsUIPanel.autoInteractGridSize();

        addComponent(this.tabAboveButtonsUIPanel);
        addComponent(this.tabBelowButtonsUIPanel);

        this.initTabButtons();
        this.initSlots();

        //默认选中最左边的
        this.setSelectedTabButton(this.tabAboveLeftButtonUI);

        this.scrollBar = new UIScrollBar();
        this.scrollBar
            .setSliderWidth(12f)
            .setSliderHeight(15f)
            .sliderGotoStart()
            .setSliderWayVisible(false)
            .setPosition(175f, 8f + this.getItemTabBackgroundRenderDeltaY());

        addComponent(this.scrollBar);
    }

    /**
     * 初始化所有物品组页面按钮
     * */
    private void initTabButtons () {
        this.tabButtons = new Array<>();

        //中间的按钮的贴图是公用的
        Resource<TextureRegion> tabAboveMiddleResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle"),
            Fight.UITexturePath("tab/tab_above_middle.png")
        );
        Resource<TextureRegion> tabAboveMiddleSelectedResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle_selected"),
            Fight.UITexturePath("tab/tab_above_middle_selected.png")
        );
        Resource<TextureRegion> tabBelowMiddleResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_below_middle"),
            Fight.UITexturePath("tab/tab_below_middle.png")
        );
        Resource<TextureRegion> tabBelowMiddleSelectedResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_below_middle_selected"),
            Fight.UITexturePath("tab/tab_below_middle_selected.png")
        );

        this.tabAboveLeftButtonUI = new TabButtonUI(TabButtonUI.Type.ABOVE, this,
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left"),
                Fight.UITexturePath("tab/tab_above_left.png")
            ),
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left_selected"),
                Fight.UITexturePath("tab/tab_above_left_selected.png")
            )
        );
        this.tabAboveMiddleButtonUI1 = new TabButtonUI(TabButtonUI.Type.ABOVE, this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabAboveMiddleButtonUI2 = new TabButtonUI(TabButtonUI.Type.ABOVE, this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabAboveMiddleButtonUI3 = new TabButtonUI(TabButtonUI.Type.ABOVE, this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);
        this.tabAboveRightButtonUI = new TabButtonUI(TabButtonUI.Type.ABOVE, this,
            tabAboveMiddleResource,
            tabAboveMiddleSelectedResource);

        this.tabAboveLeftButtonUI.setPosition(0f, 0f);
        this.tabAboveMiddleButtonUI1.setPosition(this.tabAboveLeftButtonUI.getWidth() + 1f, 0f);
        this.tabAboveMiddleButtonUI2.setPosition(
            this.tabAboveMiddleButtonUI1.getX() + this.tabAboveMiddleButtonUI1.getWidth() + 1f, 0f
        );
        this.tabAboveMiddleButtonUI3.setPosition(
            this.tabAboveMiddleButtonUI2.getX() + this.tabAboveMiddleButtonUI2.getWidth() + 1f, 0f
        );
        this.tabAboveRightButtonUI.setPosition(
            this.tabAboveMiddleButtonUI3.getX() + this.tabAboveMiddleButtonUI3.getWidth() + 1f, 0f
        );

        this.tabBelowLeftButtonUI = new TabButtonUI(TabButtonUI.Type.BELOW, this,
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_below_left"),
                Fight.UITexturePath("tab/tab_below_left.png")
            ),
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_below_left_selected"),
                Fight.UITexturePath("tab/tab_below_left_selected.png")
            )
        );
        this.tabBelowMiddleButtonUI1 = new TabButtonUI(TabButtonUI.Type.BELOW, this,
            tabBelowMiddleResource,
            tabBelowMiddleSelectedResource);
        this.tabBelowMiddleButtonUI2 = new TabButtonUI(TabButtonUI.Type.BELOW, this,
            tabBelowMiddleResource,
            tabBelowMiddleSelectedResource);
        this.tabBelowMiddleButtonUI3 = new TabButtonUI(TabButtonUI.Type.BELOW, this,
            tabBelowMiddleResource,
            tabBelowMiddleSelectedResource);
        this.tabBelowRightButtonUI = new TabButtonUI(TabButtonUI.Type.BELOW, this,
            tabBelowMiddleResource,
            tabBelowMiddleSelectedResource);

        this.tabBelowLeftButtonUI.setPosition(0f, 0f);
        this.tabBelowMiddleButtonUI1.setPosition(this.tabBelowLeftButtonUI.getWidth() + 1f, 0f);


        //把这些页面按钮加进去
        this.addTabAboveButton(this.tabAboveLeftButtonUI);
        this.addTabAboveButton(this.tabAboveMiddleButtonUI1);
        this.addTabAboveButton(this.tabAboveMiddleButtonUI2);
        this.addTabAboveButton(this.tabAboveMiddleButtonUI3);
        this.addTabAboveButton(this.tabAboveRightButtonUI);

        this.addTabBelowButton(this.tabBelowLeftButtonUI);
        this.addTabBelowButton(this.tabBelowMiddleButtonUI1);
        /*this.addTabBelowButton(this.tabBelowMiddleButtonUI2);
        this.addTabBelowButton(this.tabBelowMiddleButtonUI3);
        this.addTabBelowButton(this.tabBelowRightButtonUI);*/

        //设置每一个物品组页面对应的物品组
        this.tabAboveLeftButtonUI.setDisplayItemGroup(ItemGroups.NATURE_BLOCK_ITEM);
        this.tabAboveMiddleButtonUI1.setDisplayItemGroup(ItemGroups.COLOR_BLOCK_ITEM);
        this.tabAboveMiddleButtonUI2.setDisplayItemGroup(ItemGroups.EQUIPMENT_ITEM);
        this.tabAboveMiddleButtonUI3.setDisplayItemGroup(ItemGroups.WEAPON_ITEM);
        this.tabAboveRightButtonUI.setDisplayItemGroup(ItemGroups.MATERIAL_ITEM);

        this.tabBelowLeftButtonUI.setDisplayItemGroup(ItemGroups.TOOL_BLOCK_ITEM);
        this.tabBelowMiddleButtonUI1.setDisplayItemGroup(ItemGroups.FOOD_ITEM);
    }

    /**
     * 添加上方的物品组页面按钮
     * */
    private void addTabAboveButton (TabButtonUI tabButtonUI) {
        this.getTabAboveButtonsUIPanel().addComponent(tabButtonUI);
        this.getTabButtons().add(tabButtonUI);
    }

    /**
     * 添加下方的物品组页面按钮
     * */
    private void addTabBelowButton (TabButtonUI tabButtonUI) {
        this.getTabBelowButtonsUIPanel().addComponent(tabButtonUI);
        this.getTabButtons().add(tabButtonUI);
    }

    private void initSlots () {
        this.createSlots = new Array<>();

        float trueHeight = SlotUI.SLOT_HEIGHT + 2;
        float trueWidth = SlotUI.SLOT_WIDTH + 2;
        float deltaY = this.getItemTabBackgroundRenderDeltaY();

        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            addComponent(new PlayerSlotUI(getPlayerSystem(), index, 9 + (index * trueWidth), 8 + deltaY));
        }

        //创造槽位
        int slotIndex = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addCreateSlotUI(
                    new CreateSlotUI(
                        9 + (x * trueWidth),
                        102 - y * trueHeight + deltaY)
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
        batch.draw(this.getTabBackground(),
            getAbsX(), getAbsY() + this.getItemTabBackgroundRenderDeltaY(),
            TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT
        );

        //绘制其他子组件
        super.draw(batch, parent);
    }

    /**
     * 获取背景图片渲染的Y坐标偏移
     * */
    public float getItemTabBackgroundRenderDeltaY () {
        return TabButtonUI.BUTTON_HEIGHT;
    }

    public UIPanel getTabAboveButtonsUIPanel () {
        return this.tabAboveButtonsUIPanel;
    }

    public PlayerCreateTabUIPanel setTabAboveButtonsUIPanel (UIPanel tabAboveButtonsUIPanel) {
        this.tabAboveButtonsUIPanel = tabAboveButtonsUIPanel;
        return this;
    }

    public UIPanel getTabBelowButtonsUIPanel () {
        return this.tabBelowButtonsUIPanel;
    }

    public PlayerCreateTabUIPanel setTabBelowButtonsUIPanel (UIPanel tabBelowButtonsUIPanel) {
        this.tabBelowButtonsUIPanel = tabBelowButtonsUIPanel;
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

    public PlayerCreateTabUIPanel setTabBackgroundTextureRegionResource (Resource<TextureRegion> tabBackgroundTextureRegionResource) {
        this.tabBackgroundTextureRegionResource = tabBackgroundTextureRegionResource;
        return this;
    }

    public Array<CreateSlotUI> getCreateSlots () {
        return createSlots;
    }

    public PlayerCreateTabUIPanel setCreateSlots (Array<CreateSlotUI> createSlots) {
        this.createSlots = createSlots;
        return this;
    }

    public TabButtonUI getTabAboveLeftButtonUI () {
        return tabAboveLeftButtonUI;
    }

    public PlayerCreateTabUIPanel setTabAboveLeftButtonUI (TabButtonUI tabAboveLeftButtonUI) {
        this.tabAboveLeftButtonUI = tabAboveLeftButtonUI;
        return this;
    }

    public TabButtonUI getTabAboveMiddleButtonUI1 () {
        return tabAboveMiddleButtonUI1;
    }

    public PlayerCreateTabUIPanel setTabAboveMiddleButtonUI1 (TabButtonUI tabAboveMiddleButtonUI1) {
        this.tabAboveMiddleButtonUI1 = tabAboveMiddleButtonUI1;
        return this;
    }

    public TabButtonUI getTabAboveMiddleButtonUI2 () {
        return tabAboveMiddleButtonUI2;
    }

    public PlayerCreateTabUIPanel setTabAboveMiddleButtonUI2 (TabButtonUI tabAboveMiddleButtonUI2) {
        this.tabAboveMiddleButtonUI2 = tabAboveMiddleButtonUI2;
        return this;
    }

    public TabButtonUI getTabAboveMiddleButtonUI3 () {
        return tabAboveMiddleButtonUI3;
    }

    public PlayerCreateTabUIPanel setTabAboveMiddleButtonUI3 (TabButtonUI tabAboveMiddleButtonUI3) {
        this.tabAboveMiddleButtonUI3 = tabAboveMiddleButtonUI3;
        return this;
    }

    public TabButtonUI getTabAboveRightButtonUI () {
        return tabAboveRightButtonUI;
    }

    public PlayerCreateTabUIPanel setTabAboveRightButtonUI (TabButtonUI tabAboveRightButtonUI) {
        this.tabAboveRightButtonUI = tabAboveRightButtonUI;
        return this;
    }

    public PlayerCreateTabUIPanel setScrollBar (UIScrollBar scrollBar) {
        this.scrollBar = scrollBar;
        return this;
    }
}
