package ttk.muxiuesd.world.wall;

public class WallSmoothStone extends Wall {
    public WallSmoothStone () {
        super(new Property().setFriction(1f), "block/smooth_stone.png");
    }
}
