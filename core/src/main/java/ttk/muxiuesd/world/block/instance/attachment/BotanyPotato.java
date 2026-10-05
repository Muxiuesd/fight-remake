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

    public BotanyPotato () {
        super(createProperty());
    }

    @Override
    public void tick (World world, float delta) {
        //只有0-3这些阶段，大于就跳过
        if (getGrowLevel() >= 3) return;

        TimeSystem timeSystem = world.getSystem(TimeSystem.class);
        //植物需要在白天生长
        if (timeSystem.isDay() && MathUtils.random() > 0.999f) {
            growLevelIncrease(1);
        }
    }

    @Override
    protected BotanyPotato createSelf () {
        return new BotanyPotato();
    }
}
