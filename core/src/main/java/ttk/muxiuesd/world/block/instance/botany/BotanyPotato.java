package ttk.muxiuesd.world.block.instance.botany;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 土豆
 * <p>
 * 不同生长等级的贴图由植物渲染器持有（见 Blocks.registerBotany）
 * */
public class BotanyPotato extends Botany {

    public BotanyPotato () {
        super(new Property());
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
