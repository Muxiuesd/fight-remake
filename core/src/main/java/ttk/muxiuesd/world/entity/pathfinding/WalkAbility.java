package ttk.muxiuesd.world.entity.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.chunk.Chunk;

/**
 * 可走性判定工具
 * <p>
 * 供寻路与实体生成共用：判断"实体中心位于某位置时，碰撞箱是否不与任何障碍重叠"
 */
public class WalkAbility {
    /**
     * 判断实体能否站在某位置（实体中心坐标）
     * <p>
     * 规则：区块已加载、该格无墙、方块可行走（不可走方块中可游泳的方块对 canSwim 实体放行）、
     * 按实体半径膨胀（周围格无墙，保证碰撞箱不嵌墙）
     * @param cs 区块系统
     * @param worldX 实体中心世界坐标
     * @param worldY 实体中心世界坐标
     * @param entityRadius 实体碰撞箱半径（世界单位，宽高最大值的一半）
     * @param canSwim 实体是否可游泳（可游泳则水等可游泳方块可站立）
     */
    public static boolean canStand (ChunkSystem cs, float worldX, float worldY,
                                    float entityRadius, boolean canSwim) {
        int gx = (int) Math.floor(worldX);
        int gy = (int) Math.floor(worldY);

        //注意：必须用 float 重载（世界坐标→内部换算区块坐标），int 重载是把参数当区块坐标
        Chunk chunk = cs.getChunk((float) gx, (float) gy);
        //区块未加载：不可站立
        if (chunk == null) return false;

        GridPoint2 local = Chunk.worldToChunk(gx, gy);
        //墙：不可站立
        if (chunk.getWall(local.x, local.y) != null) return false;

        var block = chunk.getBlock(local.x, local.y);
        //方块为 null（旧存档缺格）：不可站立
        if (block == null) return false;

        boolean walkable = block.getProperty().isWalkable();
        //不可走方块中，可游泳方块对 canSwim 实体放行
        if (!walkable && canSwim && block.getProperty().isSwimmable()) {
            walkable = true;
        }
        if (!walkable) return false;

        //障碍膨胀：实体中心距离墙 >= 实体半径，保证碰撞箱不嵌墙
        int inflate = (int) Math.ceil(entityRadius + 0.5f) - 1;
        if (inflate > 0) {
            for (int dy = -inflate; dy <= inflate; dy++) {
                for (int dx = -inflate; dx <= inflate; dx++) {
                    Chunk checkChunk = cs.getChunk((float) (gx + dx), (float) (gy + dy));
                    if (checkChunk == null) continue;
                    GridPoint2 checkLocal = Chunk.worldToChunk(gx + dx, gy + dy);
                    if (checkChunk.getWall(checkLocal.x, checkLocal.y) != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
