package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

public class CameraFollowSystem extends WorldSystem {
    public static final float MAX_ZOOM = 10.0f;
    public static final float MIN_ZOOM = 0.3f;

    private Entity<?> follower; //相机跟随的实体，默认是玩家实体

    public CameraFollowSystem(World world) {
        super(world);
    }

    @Override
    public void initialize () {
        PlayerSystem ps = getWorld().getSystem(PlayerSystem.class);
        Player player = ps.getPlayer();
        this.setFollower(player);
        //初始化设置相机看向玩家的坐标，防止相机坐标在刚开始进入游戏的时候跳变
        PlayerCamera.INSTANCE.setPosition(player.getX(), player.getY());

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

        // 使相机跟随鼠标移动（带平滑插值，避免生硬抖动）
        Direction direction = Util.getDirection();
        Vector2 vector2 = Util.getMouseWindowPos();
        float xOffset = Math.abs(vector2.x) * direction.getX() / 300;
        float yOffset = Math.abs(vector2.y) * direction.getY() / 300;
        float targetX = follower.getX() + xOffset;
        float targetY = follower.getY() + yOffset;
        //时间相关的平滑过渡（系数与摩擦平滑一致，跟随及时不滞后）
        float lerpFactor = 1f - (float) Math.pow(0.0001, delta);
        //更新玩家相机位置
        PlayerCamera.INSTANCE.setPosition(
            MathUtils.lerp(camera.position.x, targetX, lerpFactor),
            MathUtils.lerp(camera.position.y, targetY, lerpFactor)
        );
    }

    public Entity<?> getFollower () {
        return this.follower;
    }

    public void setFollower (Entity<?> follower) {
        this.follower = follower;
    }
}
