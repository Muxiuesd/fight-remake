package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.Timer;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.PlayerDataOutput;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterPlayerDeath;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.ui.screen.PlayerHUDUIScreen;
import ttk.muxiuesd.ui.screen.PlayerUIScreen;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockAir;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.entity.player.PlayerDebugger;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 玩家系统
 * <p>
 * 游戏的玩家实体必须由此系统管理，由此系统提供玩家实体才是合法的
 * */
public class PlayerSystem extends WorldSystem {
    public static final String PLAYER_DATA_FILE_NAME = "player_data.json";

    /// 玩家启动加速半衰期（秒）：从静止起步向目标速度逼近一半所需的时间
    private static final float ACCEL_HALF_LIFE = 0.08f;

    public static boolean debugMode = true; //是否启用debug模式

    //玩家相关的GUIScreen
    public static PlayerHUDUIScreen PLAYER_HUD_SCREEN;
    public static PlayerUIScreen PLAYER_INVENTORY_SCREEN;

    private Player player;
    private Vector2 playerLastPosition;

    private Timer<?> bubbleEmitTimer;  //气泡粒子发射计时器

    public PlayerSystem(World world) {
        super(world);
        this.bubbleEmitTimer = new Timer<>(0.5f);

        WorldInfoTypes.INT.putIfNull(Fight.PLAYER_VISUAL_RANGE);
        WorldInfoTypes.FLOAT.putIfNull(Fight.PLAYER_HEARING_RANGE);
        WorldInfoTypes.FLOAT.putIfNull(Fight.PLAYER_PICKUP_RANGE);

        PLAYER_HUD_SCREEN = new PlayerHUDUIScreen(this);
        PLAYER_INVENTORY_SCREEN = new PlayerUIScreen(this);
    }

    @Override
    public void initialize () {
        //有玩家数据就读取
        if (UnifiedFileUtil.fileExists(Fight.getPathSavePlayer(), PLAYER_DATA_FILE_NAME)) {
            this.setPlayer(this.readPlayerData());
            Log.print(TAG(), "探查到玩家数据文件，读取玩家数据");
        }else {
            this.setPlayer(Entities.PLAYER.create(getWorld()));
            Log.print(TAG(), "未探查到玩家数据文件，新建玩家实体");
        }

        this.playerLastPosition = this.getPlayer().getPosition();

        GUISystem.getInstance().setCurScreen(PLAYER_HUD_SCREEN);

        Log.print(TAG(), "PlayerSystem初始化完成！");
    }

    @Override
    public void update (float delta) {
        //默认为false
        this.bubbleEmitTimer.update(delta);

        Player player = this.getPlayer();
        player.setUsingItem(false);

        if (player.isDeath()) {
            EventBus.post(EventTypes.PLAYER_DEATH, new EventPosterPlayerDeath(getWorld(), player));
            this.remakePlayer();
            return;
        }
        //玩家速度计算
        ChunkSystem cs = getManager().getSystem(ChunkSystem.class);
        Vector2 playerCenter = this.player.getCenterPos();
        //用玩家底部判定是否在水中（半身没入水中也算游泳），而不是中心点
        Block block = cs.getBlock(playerCenter.x, playerCenter.y - this.player.getHeight() / 2f);

        //玩家游泳
        if (this.bubbleEmitTimer.isReady() && block instanceof BlockWater) {
            //发射气泡粒子
            ParticleSystem pts = getManager().getSystem(ParticleSystem.class);
            pts.emitParticle(ParticleEmitters.ENTITY_SWIMMING, MathUtils.random(3, 7),
                playerCenter.set(playerCenter.x, playerCenter.y - 0.4f),
                new Vector2(MathUtils.random(1, 2), 0),
                player.getOrigin(),
                player.getSize().scl(0.2f), player.getSize().scl(0.05f),
                player.getScale(), MathUtils.random(0, 360), 2f);
        }

        this.handleInput(delta);

        //实时更新立体音效的接听者坐标为玩家的坐标
        SpatialAudioSystem.getInstance().getAudioListener().setPos(this.player.getX(), this.player.getY(), 0f);
    }

    /**
     * 键鼠输入处理
     * */
    private void handleInput (float delta) {
        Player curPlayer = this.getPlayer();

        //需要玩家当前的GUIScreen是HUD界面，并且鼠标不在UI组件上，防止同时操作两者
        if (GUISystem.getInstance().getCurScreen() == PLAYER_HUD_SCREEN
            && !GUISystem.getInstance().mouseOverUI()) {
            //玩家右键防御
            if (KeyBindings.PlayerShield.wasJustPressed()) {
                curPlayer.defendCDTimer.isReady();
                //TODO 护盾使用成功的相关操作
            }
            //左键使用物品
            if (KeyBindings.PlayerUseItem.wasJustPressed()) {
                curPlayer.setUsingItem(curPlayer.useItem(getWorld()));
            }
            //头两个物品槽（0号和1号）快捷循环
            if (KeyBindings.PlayerChangeItem.wasJustPressed()) {
                if (curPlayer.getHandIndex() == 0) curPlayer.setHandIndex(1);
                else if (curPlayer.getHandIndex() == 1) curPlayer.setHandIndex(0);
            }
            if (KeyBindings.PlayerDropItem.wasJustPressed()) {
                curPlayer.dropItem(curPlayer.getHandIndex(), 1);
            }
            if (KeyBindings.PlayerShortcutKey_1.wasJustPressed()) curPlayer.setHandIndex(0);
            if (KeyBindings.PlayerShortcutKey_2.wasJustPressed()) curPlayer.setHandIndex(1);
            if (KeyBindings.PlayerShortcutKey_3.wasJustPressed()) curPlayer.setHandIndex(2);
            if (KeyBindings.PlayerShortcutKey_4.wasJustPressed()) curPlayer.setHandIndex(3);
            if (KeyBindings.PlayerShortcutKey_5.wasJustPressed()) curPlayer.setHandIndex(4);
            if (KeyBindings.PlayerShortcutKey_6.wasJustPressed()) curPlayer.setHandIndex(5);
            if (KeyBindings.PlayerShortcutKey_7.wasJustPressed()) curPlayer.setHandIndex(6);
            if (KeyBindings.PlayerShortcutKey_8.wasJustPressed()) curPlayer.setHandIndex(7);
            if (KeyBindings.PlayerShortcutKey_9.wasJustPressed()) curPlayer.setHandIndex(8);

            //击退中不受输入控制（速度由击退物理接管，输入会覆盖击退速度）
            if (curPlayer.isKnockback()) return;

            //移动方向（放在守卫内：打开GUI时不能移动）
            int inputX = 0;
            int inputY = 0;
            if (KeyBindings.PlayerWalkUp.wasPressed()) {
                inputY += 1;
            }
            if (KeyBindings.PlayerWalkDown.wasPressed()) {
                inputY -= 1;
            }
            if (KeyBindings.PlayerWalkLeft.wasPressed()) {
                inputX -= 1;
            }
            if (KeyBindings.PlayerWalkRight.wasPressed()) {
                inputX += 1;
            }

            if (inputX != 0 || inputY != 0) {
                // 计算方向向量的长度
                float length = (float) Math.sqrt(inputX * inputX + inputY * inputY);
                // 归一化并乘以当前速度
                float playerSpeed = curPlayer.getSpeed();
                float velX = (inputX / length) * playerSpeed;
                float velY = (inputY / length) * playerSpeed;
                //启动加速：从当前速度向目标速度平滑逼近（半衰期 ACCEL_HALF_LIFE），
                //换向时自然先减速再转向；松键立即停（保持即时响应）
                float k = 1f - (float) Math.pow(0.5f, delta / ACCEL_HALF_LIFE);
                //只对仍按着的轴做加速，松开的方向立即归零（否则 lerp 指数衰减会残留速度，
                //导致只松开一个方向键时玩家仍沿该方向滑行）
                float curVelX = curPlayer.getVelX();
                float curVelY = curPlayer.getVelY();
                if (inputX != 0) {
                    curVelX = curVelX + (velX - curVelX) * k;
                } else {
                    curVelX = 0;
                }
                if (inputY != 0) {
                    curVelY = curVelY + (velY - curVelY) * k;
                } else {
                    curVelY = 0;
                }
                //这里要设置一遍速度不然后续的应用摩擦计算速度出问题
                curPlayer
                    .setVelocity(curVelX, curVelY)
                    .setCurSpeed(playerSpeed);
            }
            if (inputX == 0 && inputY == 0) {
                curPlayer.setVelocity(0, 0);
            }
        } else {
            //GUI 打开时玩家不能移动，停止速度（否则残留速度会继续滑行）
            curPlayer.setVelocity(0, 0);
        }
    }

    public void setItemStack (int index, String itemId) {
        String[] parts = itemId.split(":");
        Registrant<Item> itemReg = RegistrantGroup.getRegistrant(parts[0], Item.class);
        ItemStack stack = new ItemStack(itemReg.get(parts[1]));
        this.getPlayer().getBackpack().setItemStack(index, stack);
    }

    /**
     * 玩家重开
     * */
    private void remakePlayer () {
        //移除旧的玩家实体
        Player oldPlayer = this.getPlayer();
        EntitySystem es = getManager().getSystem(EntitySystem.class);
        es.remove(oldPlayer);

        //生成新的玩家实体
        Player newPlayer = Entities.PLAYER.create(getWorld());
        newPlayer.setEntitySystem(es);
        this.setPlayer(newPlayer);

        //重生位置安全检查：出生点被墙/水占据时向上搜索安全位置（防止卡墙/溺水）
        this.ensureSafeSpawnPosition();
        this.playerLastPosition = newPlayer.getPosition();
        es.add(newPlayer);

        //更新其他与玩家有关的配置
        CameraFollowSystem cfs = getManager().getSystem(CameraFollowSystem.class);
        cfs.setFollower(newPlayer);

        //播放复活音频
        getManager().getSystem(SoundSystem.class).playSpatialSound(Sounds.PLAYER_RESURRECTION, newPlayer);
    }

    /**
     * 确保玩家重生位置安全：向上搜索第一个可站立的位置
     * */
    private void ensureSafeSpawnPosition () {
        ChunkSystem cs = getManager().getSystem(ChunkSystem.class);
        float px = this.player.getX();
        float py = this.player.getY();
        float halfH = this.player.getHeight() / 2f;
        for (int i = 0; i < 64; i++) {
            float checkY = py + i;
            Block b = cs.getBlock(px, checkY - halfH);
            //空气方块可站立
            if (b == Blocks.ARI || b instanceof BlockAir) {
                this.player.setPosition(px, checkY);
                return;
            }
        }
        //找不到安全位置就保持默认位置
    }

    @Override
    public void dispose () {
        this.savePlayerData();
    }

    /**
     * 保存玩家数据
     * */
    public void savePlayerData () {
        JsonDataWriter dataWriter = new JsonDataWriter();
        RawObjectJsonConverter.toJson(dataWriter, Player.CODEC.encode(this.getPlayer()));

        PlayerDataOutput playerDataOutput = new PlayerDataOutput();
        playerDataOutput.output(dataWriter);

        Log.print(TAG(), "玩家数据保存成功！");
    }

    /**
     * 读取玩家数据
     * */
    public Player readPlayerData () {
        String playerJson = UnifiedFileUtil.readFileAsString(Fight.getPathSavePlayer(), PLAYER_DATA_FILE_NAME);
        RawObject playerRaw = RawObjectJsonConverter.fromJson(playerJson);
        DataResult<Player> playerResult = Player.CODEC.decode(playerRaw);
        //error 但 result 有值时也使用解码结果（如旧存档缺失新字段导致的部分失败），避免玩家数据整体丢失
        if (playerResult.result().isPresent()) {
            return playerResult.result().get();
        }
        Log.error(TAG(), "玩家读取失败！json原文：" + playerJson);
        //必须经 EntityProvider 创建（实体持有 provider，直接 new 会导致 provider 为 null）
        return Entities.PLAYER.create(getWorld());
    }


    /**
     * 获取玩家的唯一方式，其他地方获取玩家也是通过这个方法
     * */
    public Player getPlayer () {
        return this.player;
    }

    /**
     * 设置玩家实体
     * <p>
     * 不支持外部随意设置玩家实体，防止出问题，所以是私有方法
     * */
    private void setPlayer (Player newPlayer) {
        if (newPlayer != null) {
            this.player = newPlayer;
            if (debugMode) {
                PlayerDebugger.items(newPlayer);
            }
        }
    }

    /**
     * 检查玩家是否移动
     * */
    public boolean playerMoved () {
        Vector2 lp = this.playerLastPosition;
        Vector2 np = this.getPlayer().getPosition();
        boolean result = !lp.equals(np);
        if (result) this.playerLastPosition = np;
        return result;
    }

    private void test () {
        Registries.ITEM.getMap().values().forEach(item -> {
            ItemStack itemStack = new ItemStack(item);
            ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Entities.ITEM_ENTITY, this.getPlayer().getEntitySystem());
            itemEntity.setItemStack(itemStack);
            Direction direction = this.player.getDirection();
            itemEntity.setVelocity(direction.toVector2().scl(MathUtils.random(0.7f, 1.2f)));
            itemEntity.setSpeed(3f);
            itemEntity.setPosition(this.player.getCenterPos());
        });

        Array<ItemEntity> entityArray = this.getPlayer().getEntitySystem().getEntityArray(EntityTypes.ITEM_ENTITY);
        System.out.println(entityArray.size);
    }
}
