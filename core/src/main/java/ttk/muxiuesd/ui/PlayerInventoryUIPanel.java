package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.registry.EquipmentTypes;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.PlayerItemSlotsUIPanel;
import ttk.muxiuesd.ui.components.EquipmentPlayerSlotUI;
import ttk.muxiuesd.ui.components.PlayerSlotUI;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.item.equipment.EquipmentType;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家背包容器UI面板
 * */
public class PlayerInventoryUIPanel extends PlayerItemSlotsUIPanel {
    /// 装备槽位的起始显示坐标（与 assets/texture/ui/inventory.png 左列 4 个格子像素对齐）
    private static final float EQUIPMENT_SLOT_START_X = 7f;
    private static final float EQUIPMENT_SLOT_START_Y = 141f;

    private TextureRegion background;

    /**
     * 装备槽位列表
     * <p>
     * 槽位在写 UI 时逐个手动添加（见 {@link #initSlots()}）。
     * 槽位自持"装备背包下标"与"接受的装备类型"，此列表用于按类型反查槽位下标。
     * */
    private final List<EquipmentPlayerSlotUI> equipmentSlots = new ArrayList<>();

    public PlayerInventoryUIPanel(PlayerSystem playerSystem, TextureRegion background, float width, float height) {
        super(playerSystem, - width / 2f, - height / 2f, width, height,
            new GridPoint2(background.getRegionWidth(), background.getRegionHeight())
        );
        this.background = background;

        this.initSlots();
    }

    /**
     * 初始化所有物品槽位
     * */
    private void initSlots () {
        float trueHeight = SlotUI.SLOT_HEIGHT;
        float trueWidth = SlotUI.SLOT_WIDTH;
        float startX = 7;
        float startY = 7;
        //快捷栏槽位
        for (int index = 0; index < 9; index++) {
            addComponent(new PlayerSlotUI(getPlayerSystem(), index, startX + (index * trueWidth), startY));
        }

        //背包内部槽位
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addComponent(new PlayerSlotUI(getPlayerSystem(), x + (y * 9) + 9,
                    startX + (x * trueWidth), 29 + y * trueHeight));
            }
        }

        //装备槽位：显示位置与装备背包下标都在这里（写 UI 时）确定，接受哪种装备类型同样在此指定
        this.addEquipmentSlot(EquipmentTypes.HELMET,     0, EQUIPMENT_SLOT_START_Y);
        this.addEquipmentSlot(EquipmentTypes.CHESTPLATE, 1, EQUIPMENT_SLOT_START_Y - trueHeight);
        this.addEquipmentSlot(EquipmentTypes.LEGGINGS,   2, EQUIPMENT_SLOT_START_Y - (trueHeight * 2));
        this.addEquipmentSlot(EquipmentTypes.BOOTS,      3, EQUIPMENT_SLOT_START_Y - (trueHeight * 3));
    }

    /**
     * 添加一个装备槽位
     * @param type  该槽位接受的装备类型（注册实例）
     * @param index 该槽位对应的装备背包下标
     * @param y     槽位在面板内的显示纵坐标
     * */
    private void addEquipmentSlot (EquipmentType type, int index, float y) {
        EquipmentPlayerSlotUI slot = new EquipmentPlayerSlotUI(
            getPlayerSystem(), index, type, EQUIPMENT_SLOT_START_X, y
        );
        this.equipmentSlots.add(slot);
        addComponent(slot);
    }

    /**
     * 按装备类型反查对应的装备背包下标
     * <p>
     * 供写入端（{@code EquipmentItemStackBehaviour}）确定"该装备应放进哪一格"。
     * 未在 UI 上配置槽位的装备类型视为没有可放置的位置，返回 -1（调用方据此拒绝装备）。
     * */
    public int getEquipmentSlotIndex (EquipmentType type) {
        for (EquipmentPlayerSlotUI slot : this.equipmentSlots) {
            if (slot.type == type) return slot.getIndex();
        }
        return -1;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        //绘制背景贴图
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());

        //绘制玩家布娃娃
        Player player = getPlayerSystem().getPlayer();
        EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(player.getID());
        //渲染器未注册时跳过绘制，避免崩溃
        if (renderer == null) return;
        EntityRenderer.Context context = renderer.getContext();
        context.x = getX() + 50f;
        context.y = getY() + 125f;
        context.width = 32f;
        context.height = 32f;
        renderer.draw(batch, player, context);
        renderer.freeContext(context);

        super.draw(batch, parent);
    }
}
