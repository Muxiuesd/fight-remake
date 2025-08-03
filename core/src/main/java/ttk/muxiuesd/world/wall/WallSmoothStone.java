package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;

public class WallSmoothStone extends Wall<WallSmoothStone> {
    public WallSmoothStone () {
        super(createProperty().setFriction(1f),
            Fight.getId("smooth_stone"),
            Fight.BlockTexturePath("smooth_stone.png"));
    }

    @Override
    public WallSmoothStone createSelf (Vector2 position) {
        WallSmoothStone wallSmoothStone = new WallSmoothStone();
        wallSmoothStone.setPosition(position.x, position.y);
        wallSmoothStone.setID(getID());
        return wallSmoothStone;
    }
}
