package ttk.muxiuesd.world.block.instance.attachment;

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
    /**
     * 生长阶段贴图有4张（stage_0 ~ stage_3），所以上限是3
     * */
    public static final int MAX_GROW_LEVEL = 3;

    public BotanyPotato () {
        super(createProperty());
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

    @Override
    protected BotanyPotato createSelf () {
        return new BotanyPotato();
    }
}
