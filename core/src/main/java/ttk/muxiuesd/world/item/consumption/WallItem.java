package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 墙体物品
 * <p>
 * 贴图直接使用墙体的贴图（id 与墙体一致，通过已注册的 id→路径映射获取）
 * */
public class WallItem extends ConsumptionItem {
    private final Wall<?> wall;

    public WallItem(final Wall<?> wall) {
        this(wall, new Property());
    }

    public WallItem (final Wall<?> wall, Property property) {
        super(property);
        this.wall = wall;
    }

    /**
     * 使用放置墙体
     * <p>
     * 与方块物品放置方块一致：目标格上有没有实体、这一格原本有没有墙体，都由 ChunkSystem 统一处理
     * */
    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Vector2 worldPosition = Util.getMouseWorldPosition();
        ChunkSystem cs = world.getSystem(ChunkSystem.class);

        //放不上来（区块没加载、空气/水方块上、目标格有实体）就返回false，物品不会被消耗
        if (!cs.placeWall(this.getWall(), worldPosition.x, worldPosition.y)) return false;

        //放置成功，播放墙体自己的放置音并报告成功（物品默认的 use 不播音效也不报成功）
        world.getSystem(SoundSystem.class).playPutSound(this.getWall(), worldPosition.x, worldPosition.y);
        return true;
    }

    public Wall<?> getWall () {
        return this.wall;
    }
}
