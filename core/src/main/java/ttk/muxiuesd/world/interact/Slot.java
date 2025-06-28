package ttk.muxiuesd.world.interact;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品交互槽位
 * */
public class Slot {
    private GridPoint2 startPos;
    private GridPoint2 size;
    private Rectangle rect; //交互区域
    private int index;
    private Inventory inventory;

    public Slot (int sx, int sy, int ex, int ey, int index) {
        this(new GridPoint2(sx, sy), new GridPoint2(ex, ey), index);
    }

    public Slot (GridPoint2 startPos, GridPoint2 size, int index) {
        this.startPos = startPos;
        this.size = size;
        this.index = index;
        this.rect = new Rectangle(startPos.x, startPos.y, size.x, size.y);
    }

    /**
     * 坐标是否再这个槽位内
     * */
    public boolean touch (GridPoint2 pos) {
        return this.touch(pos.x, pos.y);
    }

    public boolean touch (int x, int y) {
        return this.rect.contains(x, y);
    }

    public GridPoint2 getStartPos () {
        return startPos;
    }

    public void setStartPos (GridPoint2 startPos) {
        this.startPos = startPos;
    }

    public GridPoint2 getSize () {
        return size;
    }

    public void setSize (GridPoint2 size) {
        this.size = size;
    }

    public Rectangle getRect () {
        return rect;
    }

    public void setRect (Rectangle rect) {
        this.rect = rect;
    }

    public int getIndex () {
        return index;
    }

    public void setIndex (int index) {
        this.index = index;
    }

    public void setInventory (Inventory inventory) {
        if (inventory == null) {
            throw new NullPointerException("传入的容器是null！！！");
        }
        this.inventory = inventory;
    }

    public ItemStack getItemStack () {
        return this.inventory.getItemStack(this.getIndex());
    }
}
