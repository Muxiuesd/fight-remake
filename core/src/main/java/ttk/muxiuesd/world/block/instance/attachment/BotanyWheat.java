package ttk.muxiuesd.world.block.instance.attachment;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.system.TimeSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 小麦
 * */
public class BotanyWheat extends Botany {
    /**
     * 生长阶段贴图有8张（stage_0 ~ stage_7），所以上限是7
     * */
    public static final int MAX_GROW_LEVEL = 7;

    public BotanyWheat () {
        super(createProperty());
    }

    @Override
    protected BotanyWheat createSelf () {
        return new BotanyWheat();
    }

    @Override
    public void tick (World world, float delta) {
        //长到上限就跳过
        if (isFullyGrown()) return;

        TimeSystem timeSystem = world.getSystem(TimeSystem.class);
        //植物需要在白天生长
        if (timeSystem.isDay() && MathUtils.random() > 0.999f) {
            growLevelIncrease(1);
        }
    }

    @Override
    public int maxGrowLevel () {
        return MAX_GROW_LEVEL;
    }
}
