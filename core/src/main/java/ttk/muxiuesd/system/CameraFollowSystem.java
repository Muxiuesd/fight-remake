package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.camera.CameraController;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;

public class CameraFollowSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();
    private CameraController cameraController;
    private Entity follower;

    public CameraFollowSystem(World world) {
        super(world);
        MainGameScreen screen = getWorld().getScreen();
        this.cameraController = screen.cameraController;
        PlayerSystem ps = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");
        this.setFollower(ps.getPlayer());
        Log.print(TAG, "CameraFollowSystem初始化完成！");
    }

    @Override
    public void update(float delta) {
        if (this.getFollower() == null) {
            return;
        }

        // 使相机跟随鼠标移动
        Direction direction = Util.getDirection();
        Vector2 vector2 = Util.getMousePosition();
        float xOffset = Math.abs(vector2.x) * direction.getxDirection() / 300;
        float yOffset = Math.abs(vector2.y) * direction.getyDirection() / 300;
        cameraController.setPosition(follower.x + follower.width / 2 + xOffset,
            follower.y + follower.height / 2 + yOffset);
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }

    @Override
    public void dispose() {

    }

    public Entity getFollower() {
        return this.follower;
    }

    public void setFollower(Entity follower) {
        this.follower = follower;
    }
}
