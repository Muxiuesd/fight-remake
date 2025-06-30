var World = {
    /** 获取当前世界 */
    get: function () {
        var worldProvider = Java.type("ttk.muxiuesd.mod.api.world.ModWorldProvider");
        return worldProvider.getWorld();
    },
    eventGroups: {
        bulletShoot: [],
        entityAttacked: [],
        entityDead: [],
        keyInput: [],
        buttonInput: [],
        ticks: []
    },
    event: {
        add: function (eventName, callback) {
            if (eventName === "bulletShoot") {
                World.eventGroups.bulletShoot.push(callback);
            }else if (eventName === "entityAttacked") {
                World.eventGroups.entityAttacked.push(callback);
            }else if (eventName === "entityDead") {
                World.eventGroups.entityDead.push(callback);
            }else if (eventName === "keyInput") {
                World.eventGroups.keyInput.push(callback);
            } else if (eventName === "buttonInput") {
                World.eventGroups.buttonInput.push(callback);
            }else if (eventName === "worldTick") {
                World.eventGroups.ticks.push(callback);
            }
        }
    },
    /** 世界系统 */
    systems: {
        /**
         * 获取系统
         * @param systemName 系统名称
         * */
        get: function (systemName) {
            var world = World.get();
            var manager = world.getSystemManager();
            return manager.getSystem(systemName);
        }
    }
}
//以下的方法是游戏代码调用的
var callBulletShootEvent = function (world, shooter, bullet) {
    for (var i = 0; i < World.eventGroups.bulletShoot.length; i++) {
        World.eventGroups.bulletShoot[i](world, shooter, bullet);
    }
}
var callEntityAttackedEvent = function (world, attackObject, victim) {
    for (var i = 0; i < World.eventGroups.entityAttacked.length; i++) {
        World.eventGroups.entityAttacked[i](world, attackObject, victim);
    }
}
var callEntityDeadEvent = function (world, deadEntity) {
    for (var i = 0; i < World.eventGroups.entityDead.length; i++) {
        World.eventGroups.entityDead[i](world, deadEntity);
    }
}
var callWorldKeyInputEvent = function (world, key) {
    for (var i = 0; i < World.eventGroups.keyInput.length; i++) {
        World.eventGroups.keyInput[i](world, key);
    }
}
var callWorldButtonInputEvent = function (world, screenX, screenY, pointer, button) {
    //屏幕坐标系原点在游戏窗口左上角，y轴向下
    for (var i = 0; i < World.eventGroups.buttonInput.length; i++) {
        World.eventGroups.buttonInput[i](world, screenX, screenY, pointer, button);
    }
}
var callWorldTickEvent = function (world, delta) {
    for (var i = 0; i < World.eventGroups.ticks.length; i++) {
        World.eventGroups.ticks[i](world, delta);
    }
}
//到这里为止
