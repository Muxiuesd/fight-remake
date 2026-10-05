package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 骨粉
 * <p>
 * 对着农作物使用（右键）可以让它的生长等级加一，加到上限为止，用一次消耗一个。
 * <p>
 * 本类不覆写{@link ttk.muxiuesd.world.item.abs.Item#use}，所以左键用手持的骨粉不会有任何效果、
 * 也不会消耗（默认实现返回false，用不上的时候要静默失败）
 * */
public class BoneMealItem extends ConsumptionItem {

    public BoneMealItem () {
        super();
    }

    public BoneMealItem (Property property) {
        super(property);
    }

    /**
     * 对着目标位置上的农作物催熟
     * @param targetPos 鼠标指向的世界坐标
     * */
    @Override
    public boolean useOn (ItemStack itemStack, World world, LivingEntity<?> user, Vector2 targetPos) {
        ChunkSystem chunkSystem = world.getSystem(ChunkSystem.class);
        //目标位置上没有东西或者不是植物，就当作没这一回事
        Attachment attachment = chunkSystem.getAttachment(targetPos);
        if (!(attachment instanceof Botany botany)) {
            return false;
        }
        //已经长到上限就不再浪费骨粉
        if (!botany.growLevelIncreaseUpToMax(1)) {
            return false;
        }
        //借父类的实现播放物品使用音效并且返回true，消耗品据此把数量减一
        return super.useOn(itemStack, world, user, targetPos);
    }
}
