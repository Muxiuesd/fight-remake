package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
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
    private TabButtonUI tabLeftButtonUI;
    private Resource<TextureRegion> tabAboveMiddleTextureRegionResource;
    private Resource<TextureRegion> tabAboveMiddleSelectedTextureRegionResource;
    private Resource<TextureRegion> tabAboveRightTextureRegionResource;
    private Resource<TextureRegion> tabAboveRightSelectedTextureRegionResource;

    private Array<CreateSlotUI> createSlots;
    private ItemGroup curItemGroup;
    private UIScrollBar scrollBar;
    private int firstCreateSlotIndex = 0;
    private int firstTabButtonIndex = 0;    //上下两个位置各有五个物品组页面按钮，三个中间，两边各一个


    public PlayerCreateTabUIPanel (PlayerSystem playerSystem) {
        super(playerSystem,
            - (float)TAB_BACKGROUND_WIDTH / 2,  - (float)TAB_BACKGROUND_HEIGHT / 2,
            TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT,
            new GridPoint2(TAB_BACKGROUND_WIDTH, TAB_BACKGROUND_HEIGHT)
        );
        this.initTabButtons();
        this.createSlots = new Array<>();
        this.initSlots();

        this.scrollBar = new UIScrollBar();
        this.scrollBar
            .setSliderWidth(12f)
            .setSliderHeight(15f)
            .sliderGotoStart()
            .setSliderWayVisible(false)
            .setPosition(175f, 8f);

        addComponent(this.scrollBar);
    }

    private void initTabButtons () {
        this.tabLeftButtonUI = new TabButtonUI(
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left"),
                Fight.UITexturePath("tab/tab_above_left.png")
            ),
            Resource.ofTextureRegion(
                Fight.ID("player_create_tab_above_left_selected"),
                Fight.UITexturePath("tab/tab_above_left_selected.png")
            )
        );

        this.tabBackgroundTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab"),
            Fight.UITexturePath("tab/tab_items.png")
        );

        this.tabAboveMiddleTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle"),
            Fight.UITexturePath("tab/tab_above_middle.png")
        );
        this.tabAboveMiddleSelectedTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_middle_selected"),
            Fight.UITexturePath("tab/tab_above_middle_selected.png")
        );
        this.tabAboveRightTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_right"),
            Fight.UITexturePath("tab/tab_above_right.png")
        );
        this.tabAboveRightSelectedTextureRegionResource = Resource.ofTextureRegion(
            Fight.ID("player_create_tab_above_right_selected"),
            Fight.UITexturePath("tab/tab_above_right_selected.png")
        );
    }

    private void initSlots () {
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
        /// 测试：显示common物品组
        this.setTabItemGroup(ItemGroups.COMMON_ITEM);
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
        batch.draw(this.getTabBackground(), getX(), getY(), getWidth(), getHeight());

        super.draw(batch, parent);
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
