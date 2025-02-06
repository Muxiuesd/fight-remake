var World = {
    eventGroups: {
        bulletShoot: [],
        entityAttacked: [],
        entityDead: [],
        keyInput: [],
        buttonInput: []
    },
    event: {
        add: function (eventName, callback) {
            if (eventName === "bulletShoot") {
                World.eventGroups.bulletShoot.push(callback);
            }
            if (eventName === "entityAttacked") {
                World.eventGroups.entityAttacked.push(callback);
            }
            if (eventName === "entityDead") {
                World.eventGroups.entityDead.push(callback);
            }
            if (eventName === "keyInput") {
                World.eventGroups.keyInput.push(callback);
            }
            if (eventName === "buttonInput") {
                World.eventGroups.buttonInput.push(callback);
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
var callWorldKeyInputEvent = function (key) {
    for (var i = 0; i < World.eventGroups.keyInput.length; i++) {
        World.eventGroups.keyInput[i](key);
    }
}
var callWorldButtonInput = function (screenX, screenY, pointer, button) {
    //屏幕坐标系原点在游戏窗口左上角，y轴向下
    for (var i = 0; i < World.eventGroups.buttonInput.length; i++) {
        World.eventGroups.buttonInput[i](screenX, screenY, pointer, button);
    }
}
//到这里为止
