package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.equipment.EquipmentType;

/**
 * 所有装备类型的注册
 * <p>
 * <b>槽位位置不在这里定义</b>：本类只给每种装备类型一个唯一身份（id + 唯一实例）。
 * "装备背包第几格接受哪种类型、显示在什么坐标"由写 UI 的
 * {@code PlayerInventoryUIPanel.initSlots()} 决定；写入端
 * （{@code EquipmentItemStackBehaviour}）通过该面板反查槽位下标。
 * <p>
 * 本类也<b>不决定装备背包的容量</b>：容量是装备容器自身的尺寸
 * （见 {@code LivingEntity.DEFAULT_EQUIPMENT_BACKPACK_SIZE}），
 * 与"注册了多少种装备类型"没有强关联。
 * <p>
 * <b>新增装备类型的完整动作</b>：
 * <ol>
 *   <li>在此加一个静态字段（注册即生效）；</li>
 *   <li>在 {@code PlayerInventoryUIPanel.initSlots()} 里加一行装备槽位
 *       （指定装备背包下标与显示坐标）—— 该下标必须落在装备背包容量范围内；</li>
 *   <li>如有必要，扩展装备面板贴图 {@code assets/texture/ui/inventory.png}
 *       与槽位布局 —— 当前贴图只有 4 个装备格。</li>
 * </ol>
 * */
public final class EquipmentTypes {
    public static void init () {
        Log.print(EquipmentTypes.class.getName(), "装备类型注册完毕");
    }

    /// 装备类型（声明顺序仅决定注册表迭代顺序，槽位下标由 UI 决定）
    public static final EquipmentType HELMET     = register("helmet");
    public static final EquipmentType CHESTPLATE = register("chestplate");
    public static final EquipmentType LEGGINGS   = register("leggings");
    public static final EquipmentType BOOTS      = register("boots");

    /**
     * 注册一种装备类型
     * */
    public static EquipmentType register (String name) {
        Identifier identifier = Identifier.of(Fight.ID(name));
        return register(identifier, new EquipmentType());
    }

    /**
     * 最基础的注册
     * */
    public static EquipmentType register (Identifier identifier, EquipmentType type) {
        type.setIdentifier(identifier);
        return Registries.EQUIPMENT_TYPE.register(identifier, type);
    }
}
