package ttk.muxiuesd.world.spawn;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家出生点查找器
 * <p>
 * 策略：随机一个"不太大"的中心坐标（|x|,|y| ≤ 10000）→ 以此为中心螺旋向外扩散，
 * 逐个用 {@link ChunkSystem#initChunk} 隔离生成，找到第一个<b>含非水陆地格</b>的区块即作为出生区块。
 * 不依赖 biome.canSpawn：海岛或海洋区块内只要有陆地格即可出生。
 * <p>
 * 关键：探查区块通过 {@code initChunk} 生成但**不 addChunk**——不会进入 ChunkSystem 的活跃/延迟队列，
 * 也不会被写盘保存（避免探查区块残留存档导致内存与存档膨胀）。
 */
public class SpawnPointFinder {
    private final long seed;
    private final ChunkSystem cs;
    /** 随机中心坐标的范围（|x|,|y| ≤ SPREAD） */
    private static final float SPREAD = 10000f;
    /** 向外扩散的最大环数（区块） */
    private static final int MAX_RING = 256;

    public SpawnPointFinder (long seed, ChunkSystem cs) {
        this.seed = seed;
        this.cs = cs;
    }

    /**
     * 找一个合法的陆地出生点（世界坐标）
     */
    public Vector2 findLandSpawn () {
        // ① 随机一个不太大的中心世界坐标（确定性）
        float baseX = this.randRange(this.seed, this.seed ^ 0x9E3779B97F4A7C15L, SPREAD);
        float baseY = this.randRange(this.seed, this.seed ^ 0xBF58476D1CE4E5B9L, SPREAD);

        // ② 螺旋扩散找第一个含非水陆地格的区块（initChunk 隔离生成，不污染系统）
        //（不依赖 biome.canSpawn：海岛/海洋区块内含陆地格也可作为出生点）
        Chunk spawnChunk = this.scanSpawnableChunk(baseX, baseY);
        if (spawnChunk == null) return new Vector2(0, 0);

        // ③ 在该区块内随机选一个非水方块格作为出生点
        Vector2 cell = this.pickLandCell(spawnChunk);
        return cell != null ? cell : new Vector2(0, 0);
    }

    /**
     * 以 (baseX, baseY) 为中心，螺旋向外扩散寻找含陆地格的区块。
     * <p>
     * 逐个 initChunk 隔离生成，找到第一个存在非水陆地格的区块即返回。
     * initChunk 不 addChunk：不出现在系统中，不会被写盘保存。
     */
    private Chunk scanSpawnableChunk (float baseX, float baseY) {
        int centerX = this.cs.getChunkPos(baseX, baseY).x;
        int centerY = this.cs.getChunkPos(baseX, baseY).y;
        for (int ring = 0; ring < MAX_RING; ring++) {
            List<ChunkPosition> ringCells = this.ringCells(centerX, centerY, ring);
            for (ChunkPosition cp : ringCells) {
                //隔离生成该区块，检查是否含非水陆地格（不含则不作为出生点）
                Chunk candidate = this.cs.initChunk(cp.getX(), cp.getY());
                if (candidate != null && this.hasLandCell(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * 区块内是否存在非水、非空气的陆地格（海岛/海洋区块内的陆地格同样生效）
     */
    private boolean hasLandCell (Chunk chunk) {
        final boolean[] found = {false};
        chunk.traversal((x, y) -> {
            Block b = chunk.getBlock(x, y);
            if (b != null && b != Blocks.ARI && !(b instanceof BlockWater)) found[0] = true;
        });
        return found[0];
    }

    /**
     * 生成以 (cx, cy) 为中心、半径为 ring 的一圈区块坐标
     */
    private List<ChunkPosition> ringCells (int cx, int cy, int ring) {
        List<ChunkPosition> cells = new ArrayList<>();
        if (ring == 0) {
            cells.add(new ChunkPosition(cx, cy));
            return cells;
        }
        for (int i = -ring; i <= ring; i++) {
            cells.add(new ChunkPosition(cx + i, cy - ring));
            cells.add(new ChunkPosition(cx + i, cy + ring));
        }
        for (int i = -ring + 1; i < ring; i++) {
            cells.add(new ChunkPosition(cx - ring, cy + i));
            cells.add(new ChunkPosition(cx + ring, cy + i));
        }
        return cells;
    }

    /**
     * 在一个区块内随机选一个非水、非空气的方块格作为世界坐标出生点
     *
     * @return 陆地格坐标；区块内没有陆地格时返回 null（调用方跳过该区块）
     */
    private Vector2 pickLandCell (Chunk chunk) {
        List<Vector2> landCells = new ArrayList<>();
        chunk.traversal((x, y) -> {
            Block b = chunk.getBlock(x, y);
            if (b != null && b != Blocks.ARI && !(b instanceof BlockWater)) {
                landCells.add(new Vector2(chunk.getWorldX(x), chunk.getWorldY(y)));
            }
        });
        if (landCells.isEmpty()) return null;
        ChunkPosition cp = chunk.getChunkPosition();
        int idx = (int) Math.abs(this.randRange(this.seed, cp.getX() * 31L + cp.getY() * 17L + 1, 1_000_000)) % landCells.size();
        return landCells.get(idx);
    }

    /**
     * 确定性生成 [-range, range] 内的随机值（splitmix64）
     */
    private float randRange (long seed, long salt, float range) {
        long h = seed ^ salt;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        double unit = (h >>> 11) * (1.0 / 9007199254740992.0);
        return (float) (unit * range * 2.0 - range);
    }
}