package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.BlockJsonDataOutput;
import ttk.muxiuesd.data.ItemJsonDataOutput;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterWorldButtonInput;
import ttk.muxiuesd.event.poster.EventPosterWorldKeyInput;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.*;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGenFactory;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 输入处理系统
 * 按键状态的更新都在这里面
 * */
public class HandleInputSystem extends WorldSystem implements InputProcessor, IWorldChunkRender {
    public final String TAG = this.getClass().getName();

    private PlayerSystem playerSystem;
    private BlockPosition mouseBlockPosition;   //鼠标指向的方块的坐标

    public HandleInputSystem(World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.playerSystem = (PlayerSystem) getWorld().getSystemManager().getSystem("PlayerSystem");

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

        if (KeyBindings.ExitGame.wasPressed()) {
            Log.print(TAG, "游戏退出！");
            Gdx.app.exit();
        }
        // C键控制区块边界是否绘制
        if (KeyBindings.ChunkBoundaryDisplay.wasJustPressed()) {
            cs.chunkEdgeRender = !cs.chunkEdgeRender;
        }
        if (KeyBindings.HitboxDisplay.wasJustPressed()) {
            cs.wallHitboxRender = !cs.wallHitboxRender;
        }

        if (KeyBindings.PlayerPositionPrint.wasJustPressed()) {
            BlockPosition pbp = this.getPlayerBlockPosition();
            ChunkPosition pcp = cs.getPlayerChunkPosition();

            Log.print(TAG, "玩家所在区块坐标：" + pcp.getX() + "," + pcp.getY());
            Log.print(TAG, "玩家所在方块坐标：" + pbp.getX() + "," + pbp.getY());
            Log.print(TAG, "玩家脚下的方块为：" + block.getClass().getName());
        }

        Vector2 mouseWorldPosition = Util.getMouseWorldPosition();
        Block mouseBlock = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);

        if (KeyBindings.PlayerShoot.wasJustPressed()) {
            Block mb = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);
            Log.print(TAG, "鼠标选中的方块为：" + mb.getClass().getName());
        }
        if (KeyBindings.PlayerInteract.wasJustPressed()) {
            ItemStack handItemStack = player.getHandItemStack();

            if (handItemStack != null) {
                //测试
                Item item = handItemStack.getItem();
                JsonDataWriter dataWriter = new JsonDataWriter();
                dataWriter.objStart();
                item.property.getPropertiesMap().write(dataWriter);
                dataWriter.objEnd();

                new ItemJsonDataOutput().output(dataWriter);
            }
            System.out.println("==============================================");
            //写入测试
            JsonDataWriter dataWriter = new JsonDataWriter();
            dataWriter.objStart();
            mouseBlock.writeCAT(mouseBlock.getProperty().getCAT());
            mouseBlock.getProperty().getPropertiesMap().write(dataWriter);
            dataWriter.objEnd();
            new BlockJsonDataOutput().output(dataWriter);

            //读取测试
            String s = FileUtil.readFileAsString(Fight.PATH_SAVE, "block.json");
            System.out.println(s);
            JsonDataReader dataReader = new JsonDataReader(s);
            JsonValue obj = dataReader.readObj(PropertyTypes.BLOCK_SOUNDS_ID.getName());
            System.out.println(obj.toJson(JsonWriter.OutputType.json));
            JsonValue values = dataReader.readObj(PropertyTypes.CAT.getName());
            mouseBlock.readCAT(values);

            if (mouseBlock instanceof BlockWithEntity<?, ?> blockWithEntity) {

                BlockEntity blockEntity = cs.getBlockEntities().get(blockWithEntity);
                //计算交互区域网格坐标
                BlockPos blockPos = blockEntity.getBlockPos();
                GridPoint2 gridSize = blockEntity.getInteractGridSize();
                int xn = (int) ((mouseWorldPosition.x - blockPos.x) * gridSize.x);
                int yn = (int) ((mouseWorldPosition.y - blockPos.y) * gridSize.y);
                GridPoint2 interactGrid = new GridPoint2(xn, yn);

                System.out.println(interactGrid);

                if (handItemStack == null) {
                    InteractResult result = blockEntity.interact(getWorld(), player, interactGrid);
                    //TODO 空手交互事件
                } else {
                    InteractResult result = blockEntity.interactWithItem(getWorld(), player, handItemStack, interactGrid);
                    if (result == InteractResult.SUCCESS && handItemStack.getAmount() == 0) {
                        //使用成功就检测手持物品是否用完，用完就清除
                        player.backpack.clear(player.getHandIndex());
                    }
                    //TODO 手持物品交互事件
                }
            }else {
                //空手右键破坏方块
                if (handItemStack == null && mouseBlock != Blocks.ARI) {
                    Block replacedBlock = cs.replaceBlock(Blocks.ARI, mouseWorldPosition.x, mouseWorldPosition.y);
                    ItemEntity itemEntity = ItemEntityGenFactory.create(
                        player.getEntitySystem(),
                        mouseWorldPosition,
                        new ItemStack(Gets.ITEM(replacedBlock.getID()), 1)
                    );
                    itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);
                }
            }
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        this.renderBlockCheckBox(batch);
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
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
        Vector2 wp = Util.getMouseWorldPosition();
        return new BlockPosition((int) Math.floor(wp.x), (int) Math.floor(wp.y));
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

    @Override
    public int getRenderPriority () {
        return 5000;
    }
}
