package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.audio.SoundInstance;
import ttk.muxiuesd.audio.SpatialSoundInstance;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

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
    private Array<SpatialSoundInstance> activeSpatialSounds;    //正在播放的空间音效

    private String curWalkSoundId;
    private SpatialSoundInstance curWalkSound;

    public SoundEffectSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.activeSounds = new LinkedHashMap<>();
        this.activeSpatialSounds = new Array<>();

        this.ps = getWorld().getSystem(PlayerSystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
        this.es = getWorld().getSystem(EntitySystem.class);

        String[] devices = Gdx.audio.getAvailableOutputDevices();
        StringBuilder deviceName = new StringBuilder();
        for (int i = 0; i < devices.length; i++) {
            if (i + 1 == devices.length) {
                deviceName.append(devices[i]);
                break;
            }
            deviceName.append(devices[i]).append(" | ");
        }
        Log.print(TAG(), "游戏音效系统加载完成，可用的音频播放设备有：\n" + deviceName);
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
            String walkSoundId = underfootBlock.getProperty().getSounds().getID(BlockSoundsID.Type.WALK);
            //检测方块不一样时curWalkSoundId
            if ((this.curWalkSoundId != null) && (!Objects.equals(this.curWalkSoundId, walkSoundId))) {
                //先停止先前的音效
                this.stopPlayerWalkSound();
                //再播放新的音效
                this.startPlayerWalkSound(walkSoundId);
            }
            if (!this.activeSpatialSounds.contains(this.curWalkSound, true)) {
                this.startPlayerWalkSound(walkSoundId);
            }
        }else if (!this.ps.playerMoved() && this.activeSpatialSounds.contains(this.curWalkSound, true)) {
            //如果玩家停止了，但是音效在播放
            this.stopPlayerWalkSound();
            this.curWalkSoundId = null;
        }
        //System.out.println(this.activeSpatialSounds.size);
    }

    private void startPlayerWalkSound (String walkSoundId) {
        Music sound = AudioLoader.getInstance().getMusic(walkSoundId);
        this.curWalkSound = new SpatialSoundInstance(sound, ps.getPlayer(), ps.getPlayer());
        this.activeSpatialSounds.add(this.curWalkSound);
        this.curWalkSoundId = walkSoundId;
    }

    private void stopPlayerWalkSound () {
        this.curWalkSound.stop();
        this.activeSpatialSounds.removeValue(this.curWalkSound, true);
        this.curWalkSound = null;
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
    }

    public void newSpatialSound (Audio audio, Entity<?> sounder) {
        this.newSpatialSound(audio.getId(), sounder);
    }
    /**
     * 新建一个空间音效
     * @param id 音效的Id，但必须是Music
     * @param sounder 发出声音的实体
     * */
    public void newSpatialSound (String id, Entity<?> sounder) {
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
