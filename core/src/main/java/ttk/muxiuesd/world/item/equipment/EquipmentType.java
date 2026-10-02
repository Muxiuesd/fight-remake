package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.ID;

/**
 * 装备类型（注册元素）
 * <p>
 * 每种装备类型在游戏中只有唯一实例，由 {@code EquipmentTypes} 在注册期创建并注册；
 * 装备物品持有它（{@link EquipmentItem#getEquipmentType()}），装备槽位也持有它，
 * 只有当物品持有的实例与槽位持有的实例是同一个时，该物品才能放入该槽位。
 * <p>
 * 本类只承载"类型身份"（id），<b>不承载槽位位置</b>：
 * 槽位位置（装备背包下标与屏幕坐标）由写 UI 的
 * {@code PlayerInventoryUIPanel.initSlots()} 决定。
 * */
public class EquipmentType implements ID<EquipmentType> {
    /// 类型标识，注册期由注册方法给定，注册过后禁止修改
    private Identifier identifier;

    public EquipmentType () {}

    @Override
    public String getID () {
        return this.identifier == null ? null : this.identifier.getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    @Override
    public EquipmentType setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException(
                "Identifier 已设置，禁止修改！装备类型：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
        return this;
    }

    @Override
    public String toString () {
        return "EquipmentType{" + this.getID() + "}";
    }
}
