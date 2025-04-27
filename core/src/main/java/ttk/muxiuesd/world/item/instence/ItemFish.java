package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品：鱼
 * */
public class ItemFish extends Item {
    public ItemFish () {
        super(Type.CONSUMPTION,
            new Property(){
                @Override
                public String getUseSoundId () {
                    //返回随机吃东西的声音
                    return Fight.getId("eat_" + MathUtils.random(1, 3));
                }
            }.setMaxCount(64),
            Fight.getId("fish"), Fight.ItemTexturePath("fish.png"));
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        Log.print(this.toString(), user.getID() + " 在吃鱼");
        return super.use(world, user);
    }
}
