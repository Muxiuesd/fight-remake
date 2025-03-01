package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntitiesReg;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventGroup;
import ttk.muxiuesd.world.event.abs.PlayerDeathEvent;

/**
 * 玩家系统
 * */
public class PlayerSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private Player player;
    private Vector2 playerLastPosition;

    public PlayerSystem(World world) {
        super(world);

    }

    @Override
    public void initialize () {

        this.player = (Player) EntitiesReg.get("player");
        this.playerLastPosition = this.player.getPosition();
        Log.print(TAG, "PlayerSystem初始化完成！");
    }

    @Override
    public void update (float delta) {
        if (this.player.isDeath()) {
            EventGroup<PlayerDeathEvent> eventGroup = EventBus.getInstance().getEventGroup(EventBus.EventType.PlayerDeath);
            for (PlayerDeathEvent event : eventGroup.getEvents()) {
                event.call(getWorld(), this.player);
            }
            this.remakePlayer();
            return;
        }
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

        AudioPlayer.getInstance().playMusic(Fight.getId("player_resurrection"));
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
