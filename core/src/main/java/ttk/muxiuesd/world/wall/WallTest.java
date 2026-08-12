package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.math.Vector2;

/**
 * 测试用墙体
 * */
public class WallTest extends Wall<WallTest>{
    public WallTest() {
        super(createProperty().setFriction(1f));
    }

    @Override
    public WallTest createSelf (Vector2 position) {
        WallTest wallTest = new WallTest();
        wallTest.setPos(position.x, position.y);
        return wallTest;
    }
}
