package ttk.muxiuesd.world.entity;

public enum Group {

    player(1),  //玩家相关的实体
    enemy(2),   //敌人相关的实体
    item(3)     //物品实体
    ;

    public final int bit;

    Group(int bit) {
        this.bit = bit;
    }
}
