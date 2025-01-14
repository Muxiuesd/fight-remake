package ttk.muxiuesd.util;

import com.badlogic.gdx.math.GridPoint2;

public class ChunkPosition extends GridPoint2 {
    public ChunkPosition() {
    }

    public ChunkPosition(int x, int y) {
        super.x = x;
        super.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        super.x = x;
    }

    public int getY() {
        return super.y;
    }

    public void setY(int y) {
        super.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
