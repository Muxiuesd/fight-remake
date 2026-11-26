package ttk.muxiuesd.world.entity.pathfinding;

/**
 * 寻路路径（A* 的计算结果）
 * <p>
 * 路径点是世界坐标（格子中心），实体沿路径点依次移动
 */
public class PathWay {
    private final float[] waypointsX;
    private final float[] waypointsY;

    public PathWay (float[] waypointsX, float[] waypointsY) {
        this.waypointsX = waypointsX;
        this.waypointsY = waypointsY;
    }

    /**
     * 路径点数量
     */
    public int getLength () {
        return this.waypointsX.length;
    }

    public float getWaypointX (int index) {
        return this.waypointsX[index];
    }

    public float getWaypointY (int index) {
        return this.waypointsY[index];
    }
}
