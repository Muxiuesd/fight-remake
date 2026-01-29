package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.registry.ItemGroups;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.PlayerItemSlotsUIPanel;
import ttk.muxiuesd.ui.components.*;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 玩家创造背包面板
 * */
public class PlayerCreateTabUIPanel extends PlayerItemSlotsUIPanel {
    private TextureRegion background;
    private Array<CreateSlotUI> createSlots;
    private ItemGroup curItemGroup;
    private UIScrollBar scrollBar;
    private int firstCreateSlotIndex = 0;

    public PlayerCreateTabUIPanel (PlayerSystem playerSystem, TextureRegion background, float width, float height) {
        super(playerSystem, - width / 2, - height / 2, width, height,
            new GridPoint2(background.getRegionWidth(), background.getRegionHeight())
        );
        this.background = background;
        this.createSlots = new Array<>();

        this.initSlots();

        this.scrollBar = new UIScrollBar();
        this.scrollBar
            .setSliderWidth(12f)
            .setSliderHeight(15f)
            .sliderGotoStart()
            .setPosition(175f, 8f);

        addComponent(this.scrollBar);
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

        UIScrollBar scroll = this.getScrollBar();
        float value = scroll.getSliderPathwayPos();
        ItemGroup itemGroup = this.getCurItemGroup();
        int count = itemGroup.getItemCount();
        int row = (count / 9);
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
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());

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

    public UIScrollBar getScrollBar () {
        return this.scrollBar;
    }
}
