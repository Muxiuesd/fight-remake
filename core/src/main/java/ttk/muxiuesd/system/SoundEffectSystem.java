package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.audio.SoundInstance;
import ttk.muxiuesd.audio.SpatialSoundInstance;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Entity;
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

    private LinkedHashMap<String, SoundInstance> activeSounds;  //正在播放的音效
    private Array<SpatialSoundInstance> activeSpatialSounds;//正在播放的空间音效

    private String curWalkSoundId;

    public SoundEffectSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.activeSounds = new LinkedHashMap<>();
        this.activeSpatialSounds = new Array<>();

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
        this.updatePlayerSoundEffect(delta);
        this.updateEnemySoundEffect(delta);
        this.updateEnvironmentalEffects(delta);
    }

    /**
     * 玩家相关的音效
     * */
    private void updatePlayerSoundEffect (float delta) {
        this.updatePlayerWalkSoundEffect(delta);
    }

    /**
     *  玩家走路音效
     * */
    private void updatePlayerWalkSoundEffect (float delta) {
        if (this.ps.playerMoved()) {
            Player player = this.ps.getPlayer();
            Vector2 playerCenter = player.getCenter();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            String walkSoundId = underfootBlock.getProperty().getWalkSoundId();
            //检测方块不一样时
            if ((this.curWalkSoundId != null) && (!Objects.equals(this.curWalkSoundId, walkSoundId))) {
                //先停止先前的音效
                this.stopPlayerWalkSound();
                //Log.print(TAG, "脚下方块变换");
                //再播放新的音效
                this.startPlayerWalkSound(walkSoundId);
            }
            if (!this.activeSounds.containsKey(Fight.getId("player_walk"))) {
                this.startPlayerWalkSound(walkSoundId);
            }
        }else if (!this.ps.playerMoved() && this.activeSounds.containsKey(Fight.getId("player_walk"))) {
            //如果玩家停止了，但是音效在播放
            this.stopPlayerWalkSound();
            this.curWalkSoundId = null;
        }
    }

    private void startPlayerWalkSound (String walkSoundId) {
        Sound sound = AudioLoader.getInstance().newSound(walkSoundId);
        SoundInstance instance = new SoundInstance(
            sound,
            SoundInstance.LOOPING,
            1.5f,
            1.5f,
            0);
        this.activeSounds.put(Fight.getId("player_walk"), instance);
        this.curWalkSoundId = walkSoundId;
    }

    private void stopPlayerWalkSound () {
        SoundInstance playerWalkSound = this.activeSounds.remove(Fight.getId("player_walk"));
        playerWalkSound.stop();
    }

    /**
     * 敌人音效
     * */
    private void updateEnemySoundEffect (float delta) {

    }

    /**
     * 环境音效
     * */
    private void updateEnvironmentalEffects (float delta) {
        //更新空间音效
        for (SpatialSoundInstance instance : this.activeSpatialSounds) {
            instance.update(delta);
            //TODO 音效播放完后移除

        }
        //System.out.println(this.activeSpatialSounds.size);
    }

    /**
     * 新建一个空间音效
     * @param id 音效的Id，但是Music
     * @param sounder 发出声音的实体
     * */
    public void newSpatialSound (String id, Entity sounder) {
        Music music = AudioLoader.getInstance().newMusic(id);
        SpatialSoundInstance instance = new SpatialSoundInstance(music, sounder, this.ps.getPlayer());
        //播放结束回调
        instance.setOnCompletionListener(music1 -> {
            this.activeSpatialSounds.removeValue(instance, true);
        });
        this.activeSpatialSounds.add(instance);
        //instance.play();
    }
}
