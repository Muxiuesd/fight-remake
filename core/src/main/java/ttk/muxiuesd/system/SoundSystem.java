package ttk.muxiuesd.system;

import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.SpatialSoundInstance;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

import java.util.LinkedHashMap;

/**
 * 游戏世界的空间音效系统
 * */
public class SoundSystem extends WorldSystem {

    private ChunkSystem cs;
    private PlayerSystem ps;
    private EntitySystem es;

    private LinkedHashMap<String, SpatialAudio> activeSounds;  //正在播放的音效


    private String curWalkSoundId;
    private SpatialSoundInstance curWalkSound;

    public SoundSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.activeSounds = new LinkedHashMap<>();
        this.ps = getWorld().getSystem(PlayerSystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
        this.es = getWorld().getSystem(EntitySystem.class);
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

    }

    private void startPlayerWalkSound (String walkSoundId) {

    }

    private void stopPlayerWalkSound () {

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

    }

    /**
     * 新建一个空间音效
     * @param audioHolder 音效的注册类
     * @param sounder 发出声音的实体
     * */
    public void playSpatialSound (AudioHolder audioHolder, Entity<?> sounder) {

    }

}
