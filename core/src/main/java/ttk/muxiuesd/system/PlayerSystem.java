package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.PlayerDataOutput;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterPlayerDeath;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.ui.screen.PlayerHUDScreen;
import ttk.muxiuesd.ui.screen.PlayerInventoryScreen;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.Optional;

/**
 * 玩家系统
 * */
public class PlayerSystem extends WorldSystem {
    public static final String PLAYER_DATA_FILE_NAME = "player_data.json";

    public static PlayerHUDScreen PLAYER_HUD_SCREEN;
    public static PlayerInventoryScreen PLAYER_INVENTORY_SCREEN;

    private Player player;
    private Vector2 playerLastPosition;

    private Timer<?> bubbleEmitTimer;  //气泡粒子发射计时器

    public PlayerSystem(World world) {
        super(world);
        this.bubbleEmitTimer = new Timer<>(0.5f);

        WorldInformationType.INT.putIfNull(Fight.PLAYER_VISUAL_RANGE);
        WorldInformationType.FLOAT.putIfNull(Fight.PLAYER_HEARING_RANGE);
        WorldInformationType.FLOAT.putIfNull(Fight.PLAYER_PICKUP_RANGE);

        PLAYER_HUD_SCREEN = new PlayerHUDScreen(this);
        PLAYER_INVENTORY_SCREEN = new PlayerInventoryScreen(this);
    }

    @Override
    public void initialize () {
        //有玩家数据就读取
        if (FileUtil.fileExists(Fight.PATH_SAVE_PLAYER, PLAYER_DATA_FILE_NAME)) {
            this.player = readPlayer();
        }else {
            this.player = Entities.PLAYER.create(getWorld());
        }

        this.playerLastPosition = this.player.getPosition();

        /*if (FileUtil.fileExists(Fight.PATH_SAVE_ENTITIES, "player_backpack.json")) {
            //测试用玩家背包解码
            String file = FileUtil.readFileAsString(Fight.PATH_SAVE_ENTITIES, "player_backpack.json");
            JsonDataReader dataReader = new JsonDataReader(file);
            Optional<Backpack> optional = Codecs.BACKPACK.decode(dataReader);
            if (optional.isPresent()) {
                this.player.setBackpack(optional.get());
            }
        }*/

        GUISystem.getInstance().setCurScreen(PLAYER_HUD_SCREEN);

        Log.print(TAG(), "PlayerSystem初始化完成！");
    }

    @Override
    public void update (float delta) {
        this.bubbleEmitTimer.update(delta);

        if (this.player.isDeath()) {
            EventBus.post(EventTypes.PLAYER_DEATH, new EventPosterPlayerDeath(getWorld(), this.player));
            this.remakePlayer();
            return;
        }
        //玩家速度计算
        ChunkSystem cs = getManager().getSystem(ChunkSystem.class);
        Vector2 playerCenter = this.player.getCenter();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);

        //玩家游泳
        if (this.bubbleEmitTimer.isReady() && block instanceof BlockWater) {
            //发射气泡粒子
            ParticleSystem pts = getManager().getSystem(ParticleSystem.class);
            pts.emitParticle(Fight.ID("entity_swimming"), MathUtils.random(3, 7),
                playerCenter.set(playerCenter.x, playerCenter.y - 0.4f),
                new Vector2(MathUtils.random(1, 2), 0),
                this.player.getOrigin(),
                this.player.getSize().scl(0.2f), this.player.getSize().scl(0.05f),
                this.player.getScale(), MathUtils.random(0, 360), 2f);
        }

        //this.test();

    }

    public void setItemStack (int index, String itemId) {
        String[] parts = itemId.split(":");
        Registrant<Item> itemReg = RegistrantGroup.getRegistrant(parts[0], Item.class);
        ItemStack stack = new ItemStack(itemReg.get(parts[1]));
        this.player.backpack.setItemStack(index, stack);
    }

    /**
     * 玩家重开
     * */
    private void remakePlayer () {
        //移除旧的玩家实体
        EntitySystem es = getManager().getSystem(EntitySystem.class);
        es.remove(this.player);

        //生成新的玩家实体
        this.player = Entities.PLAYER.create(getWorld());
        this.player.setEntitySystem(es);
        this.playerLastPosition = this.player.getPosition();
        es.add(player);

        //更新其他与玩家有关的配置
        CameraFollowSystem cfs = getManager().getSystem(CameraFollowSystem.class);
        cfs.setFollower(this.player);

        AudioPlayer.getInstance().playMusic(Sounds.PLAYER_RESURRECTION);
    }

    @Override
    public void dispose () {
        /*JsonDataWriter dataWriter = new JsonDataWriter();
        dataWriter.objStart();
        Codecs.BACKPACK.encode(this.getPlayer().getBackpack(), dataWriter);
        dataWriter.objEnd();

        new BackpackDataOutput("player_backpack").output(dataWriter);*/
        this.savePlayer();
    }

    /**
     * 保存玩家数据
     * */
    public void savePlayer () {
        JsonDataWriter dataWriter = new JsonDataWriter();
        dataWriter.objStart();
        Codecs.PLAYER.encode(this.getPlayer(), dataWriter);
        dataWriter.objEnd();

        new PlayerDataOutput().output(dataWriter);
    }

    /**
     * 读取玩家数据
     * */
    public Player readPlayer () {
        JsonValue playerValue = FileUtil.readJsonFile(Fight.PATH_SAVE_PLAYER, PLAYER_DATA_FILE_NAME);
        Optional<Player> optionalPlayer = Codecs.PLAYER.decode(new JsonDataReader(playerValue));
        if (optionalPlayer.isPresent()) {
            return optionalPlayer.get();
        }
        Log.error(TAG(), "玩家读取失败！json原文：" + playerValue.toString());
        return new Player(getWorld(), EntityTypes.PLAYER);
    }


    /**
     * 获取玩家的唯一方式，其他地方获取玩家也是通过这个方法
     * */
    public Player getPlayer() {
        return this.player;
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
            itemEntity.setVelocity(direction.scl(MathUtils.random(0.7f, 1.2f)));
            itemEntity.setSpeed(3f);
            itemEntity.setPosition(this.player.getCenter());
        });

        Array<ItemEntity> entityArray = this.getPlayer().getEntitySystem().getEntityArray(EntityTypes.ITEM_ENTITY);
        System.out.println(entityArray.size);
    }
}
