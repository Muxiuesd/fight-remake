package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ttk.muxiuesd.camera.CameraController;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterWorldButtonInput;
import ttk.muxiuesd.event.poster.EventPosterWorldKeyInput;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.BlockPosition;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 输入处理系统
 * 按键状态的更新都在这里面
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
        PlayerSystem ps = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");
        playerSystem = ps;

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float delta) {
        ChunkSystem cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
        Player player = playerSystem.getPlayer();
        Vector2 playerCenter = player.getCenter();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);
        //更新鼠标指向的世界坐标
        this.mouseBlockPosition = this.getMouseBlockPosition();

        //Log.print(TAG, "鼠标指向世界的方块坐标: (" + this.mouseBlockPosition.getX() + ", " + this.mouseBlockPosition.getY() + ")");
        if (KeyBindings.ExitGame.wasPressed()) {
            Log.print(TAG, "游戏退出！");
            Gdx.app.exit();
        }
        // C键控制区块边界是否绘制
        if (KeyBindings.ChunkBoundaryDisplay.wasJustPressed()) {
            cs.chunkEdgeRender = !cs.chunkEdgeRender;
        }
        if (KeyBindings.WallHitboxDisplay.wasJustPressed()) {
            cs.wallHitboxRender = !cs.wallHitboxRender;
        }

        if (KeyBindings.PlayerPositionPrint.wasJustPressed()) {
            BlockPosition pbp = this.getPlayerBlockPosition();
            ChunkPosition pcp = cs.getPlayerChunkPosition();

            Log.print(TAG, "玩家所在区块坐标：" + pcp.getX() + "," + pcp.getY());
            Log.print(TAG, "玩家所在方块坐标：" + pbp.getX() + "," + pbp.getY());
            Log.print(TAG, "玩家脚下的方块为：" + block.getClass().getName());
        }

        Vector2 mouseWorldPosition = this.getMouseWorldPosition();
        Block mouseBlock = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);

        if (KeyBindings.PlayerShoot.wasJustPressed()) {
            Log.print(TAG, "鼠标选中的方块为：" + mouseBlock.getClass().getName());
        }
        if (KeyBindings.PlayerInteract.wasJustPressed()) {
            if (mouseBlock instanceof BlockWithEntity) {
                BlockEntity blockEntity = cs.getBlockEntities().get(new BlockPos(Util.fastFloor(mouseWorldPosition.x, mouseWorldPosition.y)));
                ItemStack handItemStack = player.getHandItemStack();
                if (handItemStack == null) blockEntity.interact(getWorld(), player);
                else {
                    InteractResult result = blockEntity.interactWithItem(getWorld(), player, handItemStack);
                    if (result == InteractResult.SUCCESS && handItemStack.getAmount() == 0) {
                        //使用成功就检测手持物品是否用完，用完就清除
                        player.backpack.clear(player.getHandIndex());
                    }
                }
            }
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        this.renderBlockCheckBox(batch);
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        //EventBus.getInstance().callEvent(EventBus.EventType.KeyInput, keycode);
        EventBus.post(EventTypes.WORLD_KEY_INPUT, new EventPosterWorldKeyInput(getWorld(), keycode));
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
        //EventBus.getInstance().callEvent(EventBus.EventType.ButtonInput, screenX, screenY, pointer, button);
        EventBus.post(EventTypes.WORLD_BUTTON_INPUT,
            new EventPosterWorldButtonInput(getWorld(), screenX, screenY, pointer, button));
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
        //玩家背包物品指针循环
        Player player = playerSystem.getPlayer();
        int newIndex = player.getHandIndex() + (int) amountY;
        if (newIndex >= player.backpack.getSize()) {
            newIndex -= player.backpack.getSize();
        }else if (newIndex < 0) {
            newIndex = player.backpack.getSize() + (int) amountY;
        }
        player.setHandIndex(newIndex);
        System.out.println(newIndex);
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
        }
    }
}
