var EntityGroup = Java.type("ttk.muxiuesd.world.entity.Group");
var getGroup = function (entity) {
    //var bit = entity.group.bit;
    if (entity.group === EntityGroup.player) {
        return "Player"
    }
    if (entity.group === EntityGroup.enemy) {
        return "Enemy"
    }
    return undefined;
}
