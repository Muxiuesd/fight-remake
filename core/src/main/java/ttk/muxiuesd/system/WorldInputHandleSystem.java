package ttk.muxiuesd.system;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterWorldButtonInput;
import ttk.muxiuesd.event.poster.EventPosterWorldKeyInput;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.system.game.InputHandleSystem;
import ttk.muxiuesd.util.BlockPosition;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 世界输入处理系统
 * <p>
 * 鼠标对于世界各种元素的触摸、交互操作，键盘输入操作等等
 * */
public class WorldInputHandleSystem extends WorldSystem implements InputProcessor, IWorldChunkRender {
    public final String TAG = this.getClass().getName();

    private PlayerSystem playerSystem;
    private BlockPosition mouseBlockPosition;   //鼠标指向的方块的坐标

    public WorldInputHandleSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.playerSystem = getWorld().getSystem(PlayerSystem.class);
        //加入系统底层的输入处理器
        InputHandleSystem.getInstance().addProcessor(this);
    }

    /**
     * 需要每帧更新的操作
     * */
    @Override
    public void update(float delta) {
        ChunkSystem cs = getManager().getSystem(ChunkSystem.class);
        Player player = playerSystem.getPlayer();
        Vector2 playerCenter = player.getCenterPos();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);
        //更新鼠标指向的世界坐标
        this.mouseBlockPosition = getMouseBlockPosition();

        if (KeyBindings.Exit.wasJustPressed()) {
            //如果是玩家HUD屏幕就是退出游戏世界，回到主菜单
            if (GUISystem.getInstance().getCurScreen() == PlayerSystem.PLAYER_HUD_SCREEN) {
                Log.print(TAG(), "游戏退出！");
                FightCore.getInstance().setScreen(FightCore.getInstance().startMenuScreen);
                FightCore.getInstance().mainGameScreen.dispose();
            }else {
                //否则就把当前的屏幕调整回玩家HUD屏幕
                GUISystem.getInstance().setCurScreen(PlayerSystem.PLAYER_HUD_SCREEN);
            }
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
        //切换背包界面
        if (KeyBindings.PlayerBackpackScreen.wasJustPressed()) {
            if (GUISystem.getInstance().getCurScreen() == PlayerSystem.PLAYER_HUD_SCREEN) {
                GUISystem.getInstance().setCurScreen(PlayerSystem.PLAYER_INVENTORY_SCREEN);
            }else {
                GUISystem.getInstance().setCurScreen(PlayerSystem.PLAYER_HUD_SCREEN);
            }
        }

        //需要玩家当前的GUIScreen是HUD界面，并且鼠标不在UI组件上，防止同时操作两者
        //（背包/创造界面打开时禁止与世界交互，无论鼠标是否在组件上）
        if (GUISystem.getInstance().getCurScreen() == PlayerSystem.PLAYER_HUD_SCREEN
            && !GUISystem.getInstance().mouseOverUI()) {
            this.playerInteractWithWorld(player, cs);
        }
    }

    /**
     * 玩家与世界的交互
     * */
    private void playerInteractWithWorld (Player player, ChunkSystem cs) {
        Vector2 mouseWorldPosition = Util.getMouseWorldPosition();
        Block mouseBlock = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);
        ItemStack handItemStack = player.getHandItemStack();

        if (KeyBindings.PlayerShoot.wasJustPressed()) {
            //空手左键，且玩家并不是刚用完物品，就是破坏
            if (handItemStack == null && !player.isUsingItem()) {
                Botany botany = cs.getBotany(mouseWorldPosition);
                if (botany != null) {
                    //有植物就优先破坏植物
                    cs.destroyBotany(mouseWorldPosition);
                    Log.print(TAG, "鼠标破坏的植物为：" + botany.getClass().getName());
                }else if (mouseBlock != Blocks.ARI) {
                    //如果不是空气方块就破坏它
                    //破坏方块（也就是把对应坐标的方块替换成空气方块）
                    Block replacedBlock = cs.replaceBlock(Blocks.ARI, mouseWorldPosition.x, mouseWorldPosition.y);
                    this.dropItemEntity(player.getEntitySystem(), mouseWorldPosition, replacedBlock, 1);
                    Log.print(TAG, "鼠标破坏的方块为：" + mouseBlock.getClass().getName());
                }
            }
            //TODO 手持物品左键
        }
        if (KeyBindings.PlayerInteract.wasJustPressed()) {

            /// 与方块实体交互
            if (mouseBlock instanceof BlockWithEntity blockWithEntity) {
                BlockEntity blockEntity = cs.getBlockEntities().get(blockWithEntity);
                //计算交互区域网格坐标
                BlockPos blockPos = blockEntity.getBlockPos();
                GridPoint2 gridSize = blockEntity.getInteractGridSize();
                float xn = ((mouseWorldPosition.x - blockPos.x - Block.HITBOX_START_X_OFFSET) * gridSize.x);
                float yn = ((mouseWorldPosition.y - blockPos.y - Block.HITBOX_START_Y_OFFSET) * gridSize.y);
                GridPoint2 interactGrid = new GridPoint2((int) Util.fastRound(xn), (int) Util.fastRound(yn));

                //System.out.println(interactGrid);

                //玩家空手与方块实体交互
                if (handItemStack == null) {
                    InteractResult result = blockEntity.interact(getWorld(), player, interactGrid);
                    //TODO 空手交互事件
                } else {
                    //玩家手持物品与方块实体交互
                    InteractResult result = blockEntity.interactWithItem(getWorld(), player, handItemStack, interactGrid);
                    if (result == InteractResult.SUCCESS && handItemStack.getAmount() == 0) {
                        //使用成功就检测手持物品是否用完，用完就清除
                        player.backpack.clear(player.getHandIndex());
                    }
                    //TODO 手持物品交互事件
                }
            } else {
                /// 玩家与非方块实体的东西交互
                //玩家空手交互
                if (handItemStack == null) {
                    //先检查这个坐标上面有无墙体
                    if (cs.getWall(mouseWorldPosition) != null) {
                        //有墙体就破坏墙体
                        Wall<?> wall = cs.destroyWall(mouseWorldPosition);
                        this.dropItemEntity(player.getEntitySystem(), mouseWorldPosition, wall, 1);
                    }
                    /* else if (mouseBlock != Blocks.ARI) {
                        //破坏方块（也就是把对应坐标的方块替换成空气方块）
                        Block replacedBlock = cs.replaceBlock(Blocks.ARI, mouseWorldPosition.x, mouseWorldPosition.y);
                        this.dropItemEntity(player.getEntitySystem(), mouseWorldPosition, replacedBlock, 1);
                    }*/
                }
                //TODO 玩家手持物品交互
            }
        }
    }

    private ItemEntity dropItemEntity (EntitySystem entitySystem, Vector2 pos, ID<?> idHolder, int amount) {
        ItemEntity itemEntity = ItemEntityGetter.get(entitySystem, pos,
            new ItemStack(Gets.ITEM(idHolder.getID()), amount)
        );
        itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
        return itemEntity;
    }

    @Override
    public void batchRender (Batch batch) {
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
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
        //背包/创造界面打开时滚轮不切换快捷栏，防止误操作
        if (GUISystem.getInstance().getCurScreen() != PlayerSystem.PLAYER_HUD_SCREEN) return false;

        //玩家快捷栏指针循环
        Player player = playerSystem.getPlayer();
        int newIndex = player.getHandIndex() + (int) amountY;
        if (newIndex > 8) {
            newIndex = 0;
        }else if (newIndex < 0) {
            newIndex = 8;
        }
        player.setHandIndex(newIndex);
        return false;
    }

    /**
     * 获取玩家所在的方块坐标
     */
    public BlockPosition getPlayerBlockPosition() {
        BlockPosition bp = new BlockPosition();
        Vector2 playerCenter = playerSystem.getPlayer().getCenterPos();
        bp.setX((int) Util.fastRound(playerCenter.x));
        bp.setY((int) Util.fastRound(playerCenter.y));
        return bp;
    }

    /**
     * 获取鼠标指向的方块坐标
     * */
    public static BlockPosition getMouseBlockPosition() {
        Vector2 wp = Util.getMouseWorldPosition();
        //return new BlockPosition((int) Math.floor(wp.x), (int) Math.floor(wp.y));
        return new BlockPosition((int) Util.fastRound(wp.x), (int) Util.fastRound(wp.y));
    }

    /**
     * 绘制方块选中框
     */
    private void renderBlockCheckBox (ShapeRenderer batch) {
        if (this.mouseBlockPosition != null) {
            batch.setColor(Color.BLACK);
            batch.rect(
                this.mouseBlockPosition.getX() + Block.HITBOX_START_X_OFFSET + 0.1f,
                this.mouseBlockPosition.getY() + Block.HITBOX_START_Y_OFFSET + 0.1f,
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
