package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.PathFindingEntity;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRestState;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 河豚
 * <p>
 * 生物实体，继承寻路实体（可获得流场寻路能力）
 * */
public class PufferFish extends PathFindingEntity<PufferFish> {
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.7f, 0.7f);


    private Vector2 walkDistance;

    public PufferFish (World world, EntityType<? super PufferFish> entityType) {
        super(world, entityType, 5, 5, 1);
        //setBodyTextureRegion(getTextureRegion(Fight.ID("puffer_fish"), "fish/puffer_fish.png"));
        setBodyTextureRegionResource(Fight.ID("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        fastAddBodyHitBox();
        setSpeed(1f);
        getBackpack().addItem(new ItemStack(Items.PUFFER_FISH, 1));

        addState(Fight.ID("rest"), new PufferFishRestState());
        addState(Fight.ID("random_walk"), new PufferFishRandomWalkState());
    }

    @Override
    public void lazyInitialize () {
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
