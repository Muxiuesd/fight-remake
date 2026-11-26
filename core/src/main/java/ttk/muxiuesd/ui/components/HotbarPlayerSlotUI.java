package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerHotbarUIPanel;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 快捷栏槽位UI组件
 * */
public class HotbarPlayerSlotUI extends PlayerSlotUI {
    public static final float HOTBAR_WIDTH = 20f;
    public static final float HOTBAR_HEIGHT = 22f;
    public static final float SELECTED_HOTBAR_WIDTH  = 24f;
    public static final float SELECTED_HOTBAR_HEIGHT  = 24f;

    private TextureRegion textureRegion;


    public HotbarPlayerSlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        super(playerSystem, index, x, y, HOTBAR_WIDTH, HOTBAR_HEIGHT);
        this.textureRegion = Util.loadTextureRegion(
            Fight.ID("hotbar"),
            Fight.UITexturePath("hotbar.png")
        );
    }

    @Override
    public boolean click (GridPoint2 interactPos, int button) {
        //只有左键点击切换选中的快捷栏槽位
        if (button != Input.Buttons.LEFT) return super.click(interactPos, button);
        getPlayerSystem().getPlayer().setHandIndex(this.getIndex());

        return false;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (isVisible()) {
            //计算真正的渲染坐标
            float renderX = getX();
            float renderY = getY();
            //检查是否在UI面板上
            if (parent instanceof PlayerHotbarUIPanel panel) {
                renderX += panel.getX();
                renderY += panel.getY();
            }
            batch.draw(this.textureRegion, renderX, renderY, getWidth(), getHeight());
            Player player = getPlayerSystem().getPlayer();
            if (player == null) return;

            Backpack backpack = player.getBackpack();
            ItemStack itemStack = backpack.getItemStack(getIndex());
            if (!itemStack.isVoid()) {
                Item item = itemStack.getItem();
                ItemRenderer<Item> renderer = ItemRendererRegistry.get(item);
                //渲染器未注册（可能返回null）时不绘制该物品，避免崩溃
                if (renderer != null) {
                    ItemRenderer.Context context = renderer.getContext(
                        renderX + 2, renderY + 3,
                        16f, 16f
                    );
                    renderer.draw(batch, context, itemStack);
                    renderer.freeContext(context);
                }
                int amount = itemStack.getAmount();
                if (amount > 1) drawAmount(batch, parent, renderX, renderY, amount);
            }
        }
    }
}
