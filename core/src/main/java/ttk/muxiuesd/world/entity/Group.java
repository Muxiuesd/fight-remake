package ttk.muxiuesd.world.entity;

public enum Group {

    player(1),
    enemy(2);

    public final int bit;

    Group(int bit) {
        this.bit = bit;
    }
}
