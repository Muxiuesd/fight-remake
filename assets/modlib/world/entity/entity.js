
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
    getEnemyJavaType: function () {
        return Java.type("ttk.muxiuesd.world.entity.abs.Enemy");
    },
    getAbstractEnemy: function () {
        return Java.extend(this.getEnemyJavaType(), {});
    },
    getBulletJavaType: function () {
        return Java.type("ttk.muxiuesd.world.entity.abs.Bullet");
    },
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
