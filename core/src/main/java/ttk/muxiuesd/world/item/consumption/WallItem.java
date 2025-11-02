package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 墙体物品
 * */
public class WallItem extends ConsumptionItem {
    private final Wall<?> wall;

    public WallItem(final Wall<?> wall, String textureId) {
        this(wall, new Property().setUseSoundId(Sounds.STONE.getID(BlockSoundsID.Type.PUT)), textureId);
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
        //TODO 修复放置不准确的bug
        //检查与实体的碰撞箱是否有冲突
        boolean flag = false;
        EntitySystem es = world.getSystem(EntitySystem.class);
        Array<Entity<?>> entities = es.getEntities();
        for (Entity<?> entity : entities) {
            Rectangle hitbox = entity.getHitbox();
            if (hitbox == null) continue;

            Vector2 floor = Util.fastFloor(worldPosition.x, worldPosition.y);
            Rectangle wallHitbox = Pools.RECT.obtain().set(floor.x, floor.y, hitbox.width, hitbox.height);
            if (wallHitbox.overlaps(hitbox)) {
                flag = true;
                break;
            }
        }
        //标记为假时说明与实体碰撞箱不冲突，就执行放置
        if (!flag) {
            ChunkSystem cs = world.getSystem(ChunkSystem.class);
            if (cs.placeWall(this.getWall(), worldPosition.x, worldPosition.y)) return super.use(itemStack, world, user);
        }
        return false;
    }

    public Wall<?> getWall () {
        return this.wall;
    }
}
