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
    eventGroups: {
        bulletShoot: [],
        entityAttacked: [],
        entityDead: []
    },
    event: {
        add: function (eventName, callback) {
            if (eventName === "bulletShoot") {
                World.eventGroups.bulletShoot.push(callback);
                System.log("向bulletShoot添加事件");
            }
            if (eventName === "entityAttacked") {
                World.eventGroups.entityAttacked.push(callback);
                System.log("向entityAttacked添加事件");
            }
            if (eventName === "entityDead") {
                World.eventGroups.entityDead.push(callback);
                System.log("向entityDead添加事件");
            }
        }
    }
}
//以下的方法是游戏代码调用的
var callBulletShootEvent = function (shooter, bullet) {
    for (var i = 0; i < World.eventGroups.bulletShoot.length; i++) {
        World.eventGroups.bulletShoot[i](shooter, bullet);
    }
}
var callEntityAttackedEvent = function (attackObject, victim) {
    for (var i = 0; i < World.eventGroups.entityAttacked.length; i++) {
        World.eventGroups.entityAttacked[i](attackObject, victim);
    }
}
var callEntityDeadEvent = function (deadEntity) {
    for (var i = 0; i < World.eventGroups.entityDead.length; i++) {
        World.eventGroups.entityDead[i](deadEntity);
    }
}
//到这里为止


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
