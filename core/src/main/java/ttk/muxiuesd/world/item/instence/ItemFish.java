package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品：鱼
 * */
public class ItemFish extends ConsumptionItem {
    public ItemFish () {
        super(new Property().setMaxCount(64).setUseSoundId(Fight.getId("eat_")),
            Fight.getId("fish"), Fight.ItemTexturePath("fish.png"));
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        Log.print(this.toString(), user.getID() + " 在吃鱼");
        //随机吃鱼音效
        String useSoundId = this.property.getUseSoundId() + MathUtils.random(1, 3);
        SoundEffectSystem ses = (SoundEffectSystem)world.getSystemManager().getSystem("SoundEffectSystem");
        ses.newSpatialSound(useSoundId, user);
        return true;
    }
}
