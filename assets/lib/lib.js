var system = Java.type("java.lang.System")
var System = {
    log: function (msg) {
        system.out.println(msg);
    },
    error: function (msg) {
        system.err.println(msg);
    }
}
var logger = Java.type("ttk.muxiuesd.util.Log");
var Log = {
    tag: "Mod: ",
    print: function (msg) {
        logger.print(this.tag, msg);
    },
    error: function (msg) {
        logger.error(this.tag, msg);
    }
}

var World = {
    events: {
        bulletShoot: [],
        entityAttacked: []
    },
    event: {
        add: function (eventName, callback) {
            if (eventName === "bulletShoot") {
                World.events.bulletShoot.push(callback);
                System.log("向bulletShoot添加事件");
            }
            if (eventName === "entityAttacked") {
                World.events.entityAttacked.push(callback);
                System.log("向entity添加事件");
            }
        }
    }
}

var callBulletShootEvent = function (shooter, bullet) {
    for (var i = 0; i < World.events.bulletShoot.length; i++) {
        World.events.bulletShoot[i](shooter, bullet);
    }
}
var callEntityAttackedEvent = function (attackObject, victim) {
    for (var i = 0; i < World.events.entityAttacked.length; i++) {
        World.events.entityAttacked[i](attackObject, victim);
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
