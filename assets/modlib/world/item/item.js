var ItemType = Java.type("ttk.muxiuesd.world.item.abs.Item.Type");

var Items = {
    types: {
        COMMON: ItemType.COMMON,
        CONSUMPTION: ItemType.CONSUMPTION,
        WEAPON: ItemType.WEAPON,
        EQUIPMENT: ItemType.EQUIPMENT,
    },
    properties: {
        newProperty: function () {
            var ip = Java.type("ttk.muxiuesd.world.item.abs.Item.Property");
            return new ip;
        },
        newWeaponProperty: function () {
            var wp = Java.type("ttk.muxiuesd.world.item.abs.Weapon.WeaponProperty");
            return new wp;
        }
    },
    /**
     * 用于之后的extend扩展
     * */
    getItemJavaType: function () {
        return Java.type("ttk.muxiuesd.world.item.abs.Item");
    },
    /**
     * 无扩展继承
     * */
    getAbstractItem: function () {
        return Java.extend(this.getItemJavaType(), {});
    },
    newItem: function (type, property, textureId, texturePath) {
        var Item = this.getAbstractItem();
        return new Item(type, property, textureId, texturePath);
    },
    newItem: function (type, property, textureId) {
        var Item = this.getAbstractItem();
        return new Item(type, property, textureId);
    },
    newWeapon: function () {

    },
    newEquipment: function () {

    },
    newSupplier: function (func) {
        var supplier = Java.extend(Java.type("ttk.muxiuesd.mod.api.ModSupplier"), {
            getNew: function (){
                return func;
            }
        });
        return new supplier();
    },
    giveNewItem: function (id) {
        var api = Java.type("ttk.muxiuesd.mod.api.world.ModItems");
        return api.getItem(id);
    }
}
