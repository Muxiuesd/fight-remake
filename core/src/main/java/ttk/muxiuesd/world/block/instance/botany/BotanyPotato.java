package ttk.muxiuesd.world.block.instance.botany;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 土豆
 * */
public class BotanyPotato extends Botany {

    public BotanyPotato () {
        super(new Property());
        setGrowLevelTextureRegions(
            loadTextureRegion("potatoes_stage_0.png"),
            loadTextureRegion("potatoes_stage_1.png"),
            loadTextureRegion("potatoes_stage_2.png"),
            loadTextureRegion("potatoes_stage_3.png")
        );
    }

    @Override
    public void tick (World world, float delta) {
        TimeSystem timeSystem = world.getSystem(TimeSystem.class);
        //植物需要在白天生长
        if (timeSystem.isDay() && getGrowLevel() < 4 && MathUtils.random() > 0.999f) {
            growLevelIncrease(1);
        }
    }

    @Override
    public BotanyPotato createSelf () {
        BotanyPotato instance = new BotanyPotato();
        instance
            .setDroppedItem(getDroppedItem())
            .setID(getID());
        return instance;
    }
}
