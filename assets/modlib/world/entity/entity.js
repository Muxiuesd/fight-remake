
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
    /**
     * 用于之后的extend扩展
     * */
    getEnemyJavaType: function () {
        return Java.type("ttk.muxiuesd.world.entity.abs.Enemy");
    },
    /**
     * 无扩展继承
     * */
    getAbstractEnemy: function () {
        return Java.extend(this.getEnemyJavaType(), {});
    },
    /**
     * 用于之后的extend扩展
     * */
    getBulletJavaType: function () {
        return Java.type("ttk.muxiuesd.world.entity.abs.Bullet");
    },
    /**
     * 无扩展继承
     * */
    getAbstractBullet: function () {
        return Java.extend(this.getBulletJavaType(), {});
    },
    newBullet: function (textureId, damage, speed, maxLiveTime, initLiveTime) {
        var Bullet = this.getAbstractBullet();
        return new Bullet(textureId, damage, speed, maxLiveTime, initLiveTime);
    },
    newEnemy: function (textureId, maxHealth, curHealth, visionRange, attackRange, attackSpan, speed) {
        var Enemy = this.getAbstractEnemy();
        return new Enemy(textureId, maxHealth, curHealth, visionRange, attackRange, attackSpan, speed);
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
