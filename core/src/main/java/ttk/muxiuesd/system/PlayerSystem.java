package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterPlayerDeath;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.entity.EntitiesReg;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 玩家系统
 * */
public class PlayerSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private Player player;
    private Vector2 playerLastPosition;

    private Timer bubbleEmitTimer;  //气泡粒子发射计时器

    public PlayerSystem(World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.player = (Player) EntitiesReg.get("player");
        this.playerLastPosition = this.player.getPosition();
        this.bubbleEmitTimer = new Timer(0.5f);
        Log.print(TAG, "PlayerSystem初始化完成！");
    }

    @Override
    public void update (float delta) {
        this.bubbleEmitTimer.update(delta);

        if (this.player.isDeath()) {
            //EventBus.getInstance().callEvent(EventBus.EventType.PlayerDeath, getWorld(), player);
            EventBus.post(EventTypes.PLAYER_DEATH, new EventPosterPlayerDeath(getWorld(), this.player));
            this.remakePlayer();
            return;
        }
        //玩家速度计算
        ChunkSystem cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
        Vector2 playerCenter = this.player.getCenter();
        Block block = cs.getBlock(playerCenter.x, playerCenter.y);
        //player.curSpeed = player.speed * block.getProperty().getFriction();

        //玩家游泳
        if (this.bubbleEmitTimer.isReady() && block instanceof BlockWater) {
            //发射气泡粒子
            ParticleSystem pts = (ParticleSystem) getManager().getSystem("ParticleSystem");
            pts.emitParticle(Fight.getId("entity_swimming"), MathUtils.random(3, 7),
                playerCenter.set(playerCenter.x, playerCenter.y - 0.4f),
                new Vector2(MathUtils.random(1, 2), 0),
                this.player.getOrigin(),
                this.player.getSize().scl(0.2f), this.player.getSize().scl(0.05f),
                this.player.getScale(), MathUtils.random(0, 360), 2f);
        }
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
        EntitySystem es = (EntitySystem) getManager().getSystem("EntitySystem");
        es.remove(this.player);

        //生成新的玩家实体
        this.player = (Player) EntitiesReg.get("player");
        this.player.setEntitySystem(es);
        this.playerLastPosition = this.player.getPosition();
        es.add(player);

        //更新其他与玩家有关的配置
        CameraFollowSystem cfs = (CameraFollowSystem)getManager().getSystem("CameraFollowSystem");
        cfs.setFollower(this.player);

        AudioPlayer.getInstance().playMusic(Sounds.PLAYER_RESURRECTION);
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
}
