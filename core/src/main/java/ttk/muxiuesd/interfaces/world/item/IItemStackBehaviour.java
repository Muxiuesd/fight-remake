package ttk.muxiuesd.interfaces.world.item;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品堆栈行为逻辑
 * */
public interface IItemStackBehaviour {
    boolean use (World world, LivingEntity<?> user, ItemStack itemStack);

    /**
     * 手持此物品堆叠对着世界里的某个目标使用（右键交互）
     * <p>
     * 与{@link #use}的分工：{@code use}是"使用物品本身"，不认目标；
     * 本方法是对着鼠标指到的东西用，目标位置由调用方传入。
     * <p>
     * 默认返回false，也就是"这个物品不能对着东西用" —— 现有的物品都不受影响
     * @param targetPos 目标位置（通常是鼠标指向的世界坐标）
     * */
    default boolean useOn (World world, LivingEntity<?> user, ItemStack itemStack, Vector2 targetPos) {
        return false;
    }
}
