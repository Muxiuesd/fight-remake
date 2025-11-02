package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRestState;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 河豚
 * */
public class PufferFish extends LivingEntity<PufferFish> {
    public static final Vector2 DEFAULT_SIZE = Pools.VEC2.obtain().set(0.7f, 0.7f);


    private Vector2 walkDistance;

    public PufferFish (World world, EntityType<? super PufferFish> entityType) {
        super(world, entityType, 5, 5, 1);
        loadBodyTextureRegion(Fight.ID("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        setSpeed(1f);
        getBackpack().addItem(new ItemStack(Items.PUFFER_FISH, 1));

        addState(Fight.ID("rest"), new PufferFishRestState());
        addState(Fight.ID("random_walk"), new PufferFishRandomWalkState());
    }

    @Override
    public void initialize () {
        setState(Fight.ID("rest"));
    }

    @Override
    public void update (float delta) {
        super.update(delta);
    }

    public Vector2 getWalkDistance () {
        return this.walkDistance;
    }

    public PufferFish setWalkDistance (Vector2 walkDistance) {
        this.walkDistance = walkDistance;
        return this;
    }

    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
