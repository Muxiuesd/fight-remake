package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

public class CameraFollowSystem extends WorldSystem {
    public static final float MAX_ZOOM = 10.0f;
    public static final float MIN_ZOOM = 0.3f;

    private Entity<?> follower;

    public CameraFollowSystem(World world) {
        super(world);

    }

    @Override
    public void initialize () {
        PlayerSystem ps = getWorld().getSystem(PlayerSystem.class);
        this.setFollower(ps.getPlayer());

        Log.print(TAG(), "CameraFollowSystem初始化完成！");
    }

    @Override
    public void update(float delta) {
        if (this.getFollower() == null) return;

        //相机视野范围变化
        OrthographicCamera camera = PlayerCamera.INSTANCE.getCamera();
        if (KeyBindings.PlayerCameraZoomIn.wasPressed()) {
            camera.zoom -= delta * 2;
        }
        if (KeyBindings.PlayerCameraZoomOut.wasPressed()) {
            camera.zoom += delta * 2;
        }
        if (camera.zoom > MAX_ZOOM) camera.zoom = MAX_ZOOM;
        if (camera.zoom < MIN_ZOOM) camera.zoom = MIN_ZOOM;

        // 使相机跟随鼠标移动
        Direction direction = Util.getDirection();
        Vector2 vector2 = Util.getMouseWindowPos();
        float xOffset = Math.abs(vector2.x) * direction.getxDirection() / 300;
        float yOffset = Math.abs(vector2.y) * direction.getyDirection() / 300;
        PlayerCamera.INSTANCE.setPosition(follower.x + follower.width / 2 + xOffset,
            follower.y + follower.height / 2 + yOffset);

        /*GUICamera.INSTANCE.setPosition(
            follower.x + follower.width / 2,
            follower.y + follower.height / 2
        );*/

    }

    public Entity<?> getFollower() {
        return this.follower;
    }

    public void setFollower(Entity<?> follower) {
        this.follower = follower;
    }
}
