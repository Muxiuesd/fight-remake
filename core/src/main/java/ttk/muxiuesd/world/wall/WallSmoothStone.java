package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.math.Vector2;

public class WallSmoothStone extends Wall<WallSmoothStone> {
    public WallSmoothStone () {
        super(createProperty().setFriction(1f));
    }

    @Override
    public WallSmoothStone createSelf (Vector2 position) {
        WallSmoothStone wallSmoothStone = new WallSmoothStone();
        wallSmoothStone.setPos(position.x, position.y);
        wallSmoothStone.setIdentifier(getIdentifier());
        return wallSmoothStone;
    }
}
