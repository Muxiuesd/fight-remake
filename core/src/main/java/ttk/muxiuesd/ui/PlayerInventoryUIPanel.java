package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.PlayerItemSlotsUIPanel;
import ttk.muxiuesd.ui.components.EquipmentPlayerSlotUI;
import ttk.muxiuesd.ui.components.PlayerSlotUI;
import ttk.muxiuesd.ui.components.SlotUI;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 玩家背包容器UI面板
 * */
public class PlayerInventoryUIPanel extends PlayerItemSlotsUIPanel {
    private TextureRegion background;

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

        //装备槽位
        for (int y = 0; y < 4; y++) {
            addComponent(new EquipmentPlayerSlotUI(getPlayerSystem(), y, startX, 141 - (y * trueHeight)));
        }
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        //绘制背景贴图
        batch.draw(this.background, getX(), getY(), getWidth(), getHeight());

        //绘制玩家布娃娃
        Player player = getPlayerSystem().getPlayer();
        EntityRenderer<Entity<?>> renderer = EntityRendererRegistry.getRenderer(player.getID());
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
