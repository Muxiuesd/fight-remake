package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

public class SlotUI extends UIComponent {
    public static final float SLOT_WIDTH = 16f;
    public static final float SLOT_HEIGHT = 16f;


    private PlayerSystem playerSystem;
    private int index;  ///指向的玩家背包容器的索引


    public SlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        this(playerSystem, index, x, y, SLOT_WIDTH, SLOT_HEIGHT,
            new GridPoint2((int) SLOT_WIDTH, (int) SLOT_HEIGHT));
    }
    public SlotUI (PlayerSystem playerSystem, int index,
                  float x, float y, float width, float height,
                  GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);

        this.playerSystem = playerSystem;
        this.index = index;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (!isVisible()) return;

        float renderX = getX();
        float renderY = getY();
        if (parent != null) {
            renderX += parent.getX();
            renderY += parent.getY();
        }
        Player player = getPlayerSystem().getPlayer();
        if (player == null) return;
        ItemStack itemStack = player.getBackpack().getItemStack(this.getIndex());
        if (itemStack == null) return;

        batch.draw(itemStack.getItem().texture, renderX, renderY, getWidth(), getHeight());
    }

    public PlayerSystem getPlayerSystem() {
        return this.playerSystem;
    }

    public int getIndex() {
        return this.index;
    }
}
