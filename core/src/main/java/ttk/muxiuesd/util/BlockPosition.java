package ttk.muxiuesd.util;

/**
 * 方块坐标
 * */
public class BlockPosition {
    int x;
    int y;

    public BlockPosition() {
    }

    public BlockPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
