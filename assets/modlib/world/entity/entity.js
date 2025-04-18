
/**
 * 实体
 * */
var Entity = {
    /**
     * 获取实体所在的组名
     * */
    getGroup: function (entity) {
        var EntityGroup = Java.type("ttk.muxiuesd.world.entity.Group");
        if (entity.group === EntityGroup.player) {
            return "Player";
        }
        if (entity.group === EntityGroup.enemy) {
            return "Enemy";
        }
        if (entity.group === EntityGroup.item) {
            return "Item";
        }
        return undefined;
    },
    newBullet: function () {

    },
    newLivingEntity: function () {

    },
    newSupplier: function (func) {
        var supplier = Java.extend(Java.type("ttk.muxiuesd.mod.api.ModSupplier"), {
            getNew: function (){
                return func;
            }
        });
        return new supplier();
    }
}
