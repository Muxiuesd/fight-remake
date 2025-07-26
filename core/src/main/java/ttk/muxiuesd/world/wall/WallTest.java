package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;

/**
 * 测试用墙体
 * */
public class WallTest extends Wall<WallTest>{
    public WallTest() {
        super(createProperty().setFriction(1f),
            Fight.getId("test_wall"),
            Fight.BlockTexturePath("block_test.png"));
    }

    @Override
    public WallTest createSelf (Vector2 position) {
        WallTest wallTest = new WallTest();
        wallTest.setPosition(position.x, position.y);
        return wallTest;
    }
}
