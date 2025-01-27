package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ttk.muxiuesd.camera.CameraController;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.BlockPosition;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Player;

public class HandleInputSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private PlayerSystem playerSystem;
    private CameraController cameraController;
    private BlockPosition mouseBlockPosition;   //鼠标指向的方块的坐标

    public HandleInputSystem(World world) {
        super(world);
        MainGameScreen screen = getWorld().getScreen();
        this.cameraController = screen.cameraController;
        EntitySystem es = (EntitySystem) getWorld().getSystemManager().getSystem("EntitySystem");
        PlayerSystem ps = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");
        playerSystem = ps;
    }

    @Override
    public void update(float delta) {
        ChunkSystem cs = (ChunkSystem) getManager().getSystem("ChunkSystem");

        Player player = playerSystem.getPlayer();
        Vector2 playerCenter = player.getPlayerCenter();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);
        player.curSpeed = player.speed * block.getProperty().getFriction();
        //更新鼠标指向的世界坐标
        this.mouseBlockPosition = getMouseBlockPosition(cs);

        //Log.print(TAG, "鼠标指向世界的方块坐标: (" + this.mouseBlockPosition.getX() + ", " + this.mouseBlockPosition.getY() + ")");
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Log.print(TAG, "游戏退出！");
            Gdx.app.exit();
        }
        // C键控制区块边界是否绘制
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            cs.chunkEdgeRender = !cs.chunkEdgeRender;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            cs.wallHitboxRender = !cs.wallHitboxRender;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            BlockPosition pbp = this.getPlayerBlockPosition();
            ChunkPosition pcp = cs.getPlayerChunkPosition();

            Log.print(TAG, "玩家所在区块坐标：" + pcp.getX() + "," + pcp.getY());
            Log.print(TAG, "玩家所在方块坐标：" + pbp.getX() + "," + pbp.getY());
            Log.print(TAG, " 方块为：" + block.getClass().getName());
        }
    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        if (this.mouseBlockPosition != null) {
            batch.setColor(Color.BLACK);
            batch.rect(
                this.mouseBlockPosition.getX(),
                this.mouseBlockPosition.getY(),
                1f,
                1f);
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void dispose() {

    }

    /**
     * 获取玩家所在的方块坐标
     */
    public BlockPosition getPlayerBlockPosition() {
        BlockPosition bp = new BlockPosition();
        Vector2 playerCenter = playerSystem.getPlayer().getPlayerCenter();
        bp.setX((int) Util.fastRound(playerCenter.x));
        bp.setY((int) Util.fastRound(playerCenter.y));
        return bp;
    }

    public BlockPosition getMouseBlockPosition(ChunkSystem cs) {
        OrthographicCamera camera = cameraController.camera;
        Vector3 mp = new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), camera.position.z);
        Vector3 unproject = camera.unproject(mp);
        Vector2 wp = new Vector2(unproject.x, unproject.y);
        return new BlockPosition((int) Math.floor(wp.x), (int) Math.floor(wp.y));
    }
}
