package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ttk.muxiuesd.camera.CameraController;
import ttk.muxiuesd.key.InputBinding;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.BlockPosition;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventGroup;
import ttk.muxiuesd.world.event.abs.ButtonInputEvent;
import ttk.muxiuesd.world.event.abs.KeyInputEvent;

import java.util.HashSet;

/**
 * 输入处理系统
 * */
public class HandleInputSystem extends WorldSystem implements InputProcessor {
    public final String TAG = this.getClass().getName();

    private PlayerSystem playerSystem;
    private CameraController cameraController;
    private BlockPosition mouseBlockPosition;   //鼠标指向的方块的坐标

    public HandleInputSystem(World world) {
        super(world);
    }

    @Override
    public void initialize () {
        MainGameScreen screen = getWorld().getScreen();
        this.cameraController = screen.cameraController;
        //EntitySystem es = (EntitySystem) getWorld().getSystemManager().getSystem("EntitySystem");
        PlayerSystem ps = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");
        playerSystem = ps;

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float delta) {
        this.updateButtonStates();

        ChunkSystem cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
        Player player = playerSystem.getPlayer();
        Vector2 playerCenter = player.getCenter();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);
        //更新鼠标指向的世界坐标
        this.mouseBlockPosition = this.getMouseBlockPosition();

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
            Log.print(TAG, "玩家脚下的方块为：" + block.getClass().getName());
        }
        if (KeyBindings.PlayerShoot.wasPressed()) {
            Vector2 mouseWorldPosition = this.getMouseWorldPosition();
            Block mouseBlock = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);
            Log.print(TAG, "鼠标选中的方块为：" + mouseBlock.getClass().getName());
        }
    }

    /**
     * 更新鼠标按键状态
     * */
    private void updateButtonStates() {
        boolean[] code = new boolean[5];
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) code[Input.Buttons.LEFT] = true;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) code[Input.Buttons.RIGHT] = true;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) code[Input.Buttons.MIDDLE] = true;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.BACK)) code[Input.Buttons.BACK] = true;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.FORWARD)) code[Input.Buttons.FORWARD] = true;

        for (int i = 0; i < code.length; i++) {
            InputBinding.updateButtonState(i, code[i]);
        }
    }


    @Override
    public void renderShape(ShapeRenderer batch) {
        this.renderBlockCheckBox(batch);
    }

    @Override
    public boolean keyDown (int keycode) {
        InputBinding.updateKeyState(keycode, true);
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        //System.out.println("keyUp");
        EventGroup<KeyInputEvent> group = EventBus.getInstance().getEventGroup(EventBus.EventType.KeyInput);
        HashSet<KeyInputEvent> events = group.getEvents();
        for (KeyInputEvent event : events) {
            event.call(keycode);
        }

        InputBinding.updateKeyState(keycode, false);

        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        EventGroup<ButtonInputEvent> group = EventBus.getInstance().getEventGroup(EventBus.EventType.ButtonInput);
        HashSet<ButtonInputEvent> events = group.getEvents();
        for (ButtonInputEvent event : events) {
            event.call(screenX, screenY, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return false;
    }

    /**
     * 获取玩家所在的方块坐标
     */
    public BlockPosition getPlayerBlockPosition() {
        BlockPosition bp = new BlockPosition();
        Vector2 playerCenter = playerSystem.getPlayer().getCenter();
        bp.setX((int) Util.fastRound(playerCenter.x));
        bp.setY((int) Util.fastRound(playerCenter.y));
        return bp;
    }

    /**
     * 获取鼠标指向的方块坐标
     * */
    public BlockPosition getMouseBlockPosition() {
        Vector2 wp = this.getMouseWorldPosition();
        return new BlockPosition((int) Math.floor(wp.x), (int) Math.floor(wp.y));
    }

    /**
     * 获取鼠标指向的世界坐标
     * */
    public Vector2 getMouseWorldPosition() {
        OrthographicCamera camera = cameraController.camera;
        Vector3 mp = new Vector3(new Vector2(Gdx.input.getX(), Gdx.input.getY()), camera.position.z);
        Vector3 up = camera.unproject(mp);
        return new Vector2(up.x, up.y);
    }

    /**
     * 绘制方块选中框
     */
    private void renderBlockCheckBox (ShapeRenderer batch) {
        if (this.mouseBlockPosition != null) {
            batch.setColor(Color.BLACK);
            //batch.set(ShapeRenderer.ShapeType.Filled);
            batch.rect(
                this.mouseBlockPosition.getX() + 0.1f,
                this.mouseBlockPosition.getY() + 0.1f,
                0.8f,
                0.8f);
            batch.setColor(Color.WHITE);
            //batch.set(ShapeRenderer.ShapeType.Line);
        }
    }


}
