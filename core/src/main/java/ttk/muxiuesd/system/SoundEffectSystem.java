package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.audio.AudioManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Player;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 游戏内的音效系统
 * */
public class SoundEffectSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private ChunkSystem cs;
    private PlayerSystem ps;
    private EntitySystem es;

    private LinkedHashMap<String, Sound> playingSounds;  //正在播放的音效
    //private LinkedHashMap<Long, Sound> soundInstances;
    private String curWalkSoundId;

    public SoundEffectSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.playingSounds = new LinkedHashMap<>();
        //this.soundInstances = new LinkedHashMap<>();
        this.cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
        this.ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        this.es = (EntitySystem) getManager().getSystem("EntitySystem");

        String[] devices = Gdx.audio.getAvailableOutputDevices();
        StringBuilder deviceName = new StringBuilder();
        for (int i = 0; i < devices.length; i++) {
            if (i + 1 == devices.length) {
                deviceName.append(devices[i]);
                break;
            }
            deviceName.append(devices[i]).append(" | ");
        }
        Log.print(TAG, "游戏音效系统加载完成，可用的音频播放设备有：\n" + deviceName);
    }

    @Override
    public void update (float delta) {
        this.playerSoundEffect(delta);
        this.enemySoundEffect(delta);
    }

    /**
     * 玩家音效
     * */
    private void playerSoundEffect (float delta) {
        //玩家走路音效
        if (this.ps.playerMoved()) {
            Player player = this.ps.getPlayer();
            Vector2 playerCenter = player.getPlayerCenter();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            String walkSoundId = underfootBlock.getProperty().getWalkSoundId();
            //检测方块不一样时
            if ((this.curWalkSoundId != null) && (!Objects.equals(this.curWalkSoundId, walkSoundId))) {
                //先停止先前的音效
                this.stopPlayerWalkSound();
                Log.print(TAG, "脚下方块变换");
                //再播放新的音效
                Sound sound = AudioManager.getInstance().getSound(walkSoundId);
                sound.loop(1.5f, 1.5f, 0);
                this.curWalkSoundId = walkSoundId;
                this.playingSounds.put("player_walk", sound);
            }
            if (!this.playingSounds.containsKey("player_walk")) {
                Sound sound = AudioManager.getInstance().getSound(walkSoundId);
                sound.loop(1.5f, 1f, 0);
                this.curWalkSoundId = walkSoundId;
                this.playingSounds.put("player_walk", sound);
            }
        }else if (!this.ps.playerMoved() && this.playingSounds.containsKey("player_walk")) {
            //如果玩家停止了，但是音效在播放
            this.stopPlayerWalkSound();
            this.curWalkSoundId = null;
        }
    }

    private void stopPlayerWalkSound () {
        Sound playerWalkSound = this.playingSounds.remove("player_walk");
        playerWalkSound.stop();
    }
    /**
     * 敌人音效
     * */
    private void enemySoundEffect (float delta) {

    }

}
