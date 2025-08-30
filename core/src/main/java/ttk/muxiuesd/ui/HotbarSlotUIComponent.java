package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 快捷栏槽位UI组件
 * */
public class HotbarSlotUIComponent extends UIComponent {
    public static final float HOTBAR_WIDTH = 20f;
    public static final float HOTBAR_HEIGHT = 22f;
    public static final float SELECTED_HOTBAR_WIDTH  = 24f;
    public static final float SELECTED_HOTBAR_HEIGHT  = 24f;

    private TextureRegion textureRegion;

    private PlayerSystem playerSystem;
    private int index;  ///指向的玩家背包容器的索引

    public HotbarSlotUIComponent (PlayerSystem playerSystem, int index, float x, float y) {
        super(x, y, HOTBAR_WIDTH, HOTBAR_HEIGHT, new GridPoint2((int) HOTBAR_WIDTH, (int) HOTBAR_HEIGHT));
        this.textureRegion = Util.loadTextureRegion(
            Fight.getId("hotbar"),
            Fight.UITexturePath("hotbar.png")
        );

        this.playerSystem = playerSystem;
        this.index = index;
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        this.playerSystem.getPlayer().setHandIndex(this.getIndex());

        return super.click(interactPos);
    }

    @Override
    public void draw (Batch batch) {
        if (isVisible()) {
            batch.draw(this.textureRegion, getX(), getY(), getWidth(), getHeight());
            Player player = this.playerSystem.getPlayer();
            if (player == null) return;

            Backpack backpack = player.getBackpack();
            ItemStack itemStack = backpack.getItemStack(this.index);
            if (itemStack != null) {
                batch.draw(itemStack.getItem().texture, getX() + 2, getY() + 3, 16f, 16f);
            }
        }
    }

    @Override
    public void resize (float width, float height) {
        //让快捷栏整体贴着窗口下面
        setPosition(getX(), - height / 2);
    }

    public int getIndex () {
        return this.index;
    }
}
