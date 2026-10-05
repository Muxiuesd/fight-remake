package ttk.muxiuesd.world.block.instance.attachment;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 小麦
 * */
public class BotanyWheat extends Botany {

    public BotanyWheat () {
        super(createProperty());
    }

    @Override
    protected BotanyWheat createSelf () {
        return new BotanyWheat();
    }

    @Override
    public void tick (World world, float delta) {
        //只有0-7这些阶段，大于就跳过
        if (getGrowLevel() >= 7) return;

        TimeSystem timeSystem = world.getSystem(TimeSystem.class);
        //植物需要在白天生长
        if (timeSystem.isDay() && MathUtils.random() > 0.999f) {
            growLevelIncrease(1);
        }
    }
}
