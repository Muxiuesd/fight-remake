package ttk.muxiuesd.world.block.instance.botany;

import ttk.muxiuesd.util.Util;
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
        if (getGrowLevel() < 4 && Util.randomSin() > 0.98768834f) {
            growLevelIncrease(1);
        }
    }

    @Override
    public BotanyPotato createSelf () {
        BotanyPotato instance = new BotanyPotato();
        instance.setID(getID());
        return instance;
    }
}
