package ttk.muxiuesd.world.entity;

/**
 * 实体组
 * */
public enum Group {

    player(1),      //玩家相关的实体
    enemy(2),       //敌人相关的实体
    creature(3),    //生物相关的实体
    item(4)         //物品实体
    ;

    public final int bit;

    Group(int bit) {
        this.bit = bit;
    }
}
