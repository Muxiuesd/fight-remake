package ttk.muxiuesd.world.wall;

import ttk.muxiuesd.Fight;

public class WallSmoothStone extends Wall {
    public WallSmoothStone () {
        super(new Property().setFriction(1f),
            Fight.getId("smooth_stone"),
            Fight.BlockTexturePath("smooth_stone.png"));
    }
}
