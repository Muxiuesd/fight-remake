var system = Java.type("java.lang.System")
var System = {
    log: function (msg) {
        system.out.println(msg);
    },
    error: function (msg) {
        system.err.println(msg);
    }
}

var World = {
    events: {
        bulletShoot: []
    },
    event: {
        add: function (eventName, callback) {
            if (eventName === "bulletShoot") {
                World.events.bulletShoot.push(callback);
                System.log("向bulletShoot添加事件");
            }
        }
    }
}

var callBulletShootEvent = function (shooter, bullet) {
    for (var i = 0; i < World.events.bulletShoot.length; i++) {
        World.events.bulletShoot[i](shooter, bullet);
    }
}
var group = Java.type("ttk.muxiuesd.world.entity.Group");
var getGroup = function (entity) {
    //var bit = entity.group.bit;
    if (entity.group === group.player) {
        return "Player"
    }
    if (entity.group === group.enemy) {
        return "Enemy"
    }
    return undefined;
}
