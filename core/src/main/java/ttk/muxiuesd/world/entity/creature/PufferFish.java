package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 河豚
 * */
public class PufferFish extends LivingEntity {
    public static final Vector2 DEFAULT_SIZE = Pools.VEC2.obtain().set(0.7f, 0.7f);

    public PufferFish() {
        initialize(Group.creature, 5, 5, 1);
        loadBodyTextureRegion(Fight.getId("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
    }

    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
