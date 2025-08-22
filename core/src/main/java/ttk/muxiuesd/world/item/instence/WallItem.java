package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 墙体物品
 * */
public class WallItem extends ConsumptionItem {
    private final Wall<?> wall;

    public WallItem(final Wall<?> wall, String textureId) {
        this(wall, new Property(), textureId);
    }

    public WallItem (final Wall<?> wall, Property property, String textureId) {
        super(property, textureId);
        this.wall = wall;
    }

    /**
     * 使用放置墙体
     * */
    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Vector2 worldPosition = Util.getMouseWorldPosition();
        ChunkSystem cs = world.getSystem(ChunkSystem.class);

        if (cs.placeWall(this.getWall(), worldPosition.x, worldPosition.y)) return super.use(itemStack, world, user);
        return false;
    }

    public Wall<?> getWall () {
        return this.wall;
    }
}
