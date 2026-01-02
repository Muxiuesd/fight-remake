package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.HotbarUIPanel;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

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
    public boolean click (GridPoint2 interactPos) {
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
            if (parent instanceof HotbarUIPanel panel) {
                renderX += panel.getX();
                renderY += panel.getY();
            }
            batch.draw(this.textureRegion, renderX, renderY, getWidth(), getHeight());
            Player player = getPlayerSystem().getPlayer();
            if (player == null) return;

            Backpack backpack = player.getBackpack();
            ItemStack itemStack = backpack.getItemStack(getIndex());
            if (itemStack != null) {
                batch.draw(itemStack.getItem().textureRegion, renderX + 2, renderY + 3, 16f, 16f);
                int amount = itemStack.getAmount();
                if (amount > 1) drawAmount(batch, parent, renderX, renderY, amount);
            }
        }
    }
}
