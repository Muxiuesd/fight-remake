package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 游戏世界的空间音效系统
 * <p>
 * 只要是在游戏世界内播放的音效，就调用这个系统来播放。这里面调用播放的音频，出了游戏世界就不再播放，比如退出游戏世界后停止播放里面的一切音频。
 * */
public class SoundSystem extends WorldSystem {

    private ChunkSystem cs;
    private PlayerSystem ps;
    private EntitySystem es;

    //正在播放的音效
    private LinkedHashMap<Identifier, SpatialAudio> activeSoundsMap;
    private Array<SpatialAudio> activeSounds;
    private Array<SpatialAudio> needRemoved;

    private AudioHolder curWalkAudio;

    public SoundSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.activeSoundsMap = new LinkedHashMap<>();
        this.activeSounds = new Array<>();
        this.needRemoved = new Array<>();
        this.ps = getWorld().getSystem(PlayerSystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
        this.es = getWorld().getSystem(EntitySystem.class);
    }

    @Override
    public void update (float delta) {
        this.activeSounds.forEach( (audio) -> {
            if (!audio.isPlaying()) this.needRemoved.add(audio);
        });
        this.activeSounds.removeAll(this.needRemoved, true);
        this.needRemoved.clear();

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
        //如果玩家在移动
        if (this.ps.playerMoved()) {
            Player player = this.ps.getPlayer();
            Vector2 playerCenter = player.getCenterPos();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            AudioHolder walkAudio = underfootBlock.getProperty().getSounds().walk();
            //检测方块不一样时walkAudio是否一样
            if (! Objects.equals(this.curWalkAudio, walkAudio)) {
                //先停止先前的音效
                this.stopPlayerWalkSound();
                //再播放新的音效
                this.startPlayerWalkSound(walkAudio, player);
            }
            if (!this.activeSoundsMap.containsKey(this.curWalkAudio.getIdentifier())) {
                this.startPlayerWalkSound(walkAudio, player);
            }
        }else if (
            !this.ps.playerMoved()
            && this.curWalkAudio != null
            && this.activeSoundsMap.containsKey(this.curWalkAudio.getIdentifier())
        ) { //如果玩家停止了，但是音效在播放，就停止音效
            this.stopPlayerWalkSound();
            this.curWalkAudio = null;
        }
    }

    /**
     * 开始播放玩家走路音效
     * */
    public void startPlayerWalkSound (AudioHolder walkAudio, Player player) {
        SpatialAudio audio = this.playSpatialSound(walkAudio, player);
        this.activeSoundsMap.put(walkAudio.getIdentifier(), audio);
        this.curWalkAudio = walkAudio;
    }

    /**
     * 停止播放玩家走路音效
     * */
    public void stopPlayerWalkSound () {
        if (this.curWalkAudio != null
            && this.activeSoundsMap.containsKey(this.curWalkAudio.getIdentifier()))
        {
            SpatialAudio removed = this.activeSoundsMap.remove(this.curWalkAudio.getIdentifier());
            removed.stop();
            removed.dispose();
        }
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
     * 由实体来播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param sounderEntity 发出声音的实体
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, Entity<?> sounderEntity) {
        return this.playSpatialSound(audioHolder, sounderEntity.getSounder());
    }

    /**
     * 由方块实体来播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param blockEntity 发出声音的方块实体
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, BlockEntity blockEntity) {
        return this.playSpatialSound(audioHolder, blockEntity.getSounder());
    }

    /**
     * 播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param source      声音源
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, SpatialAudioSource source) {
        return SpatialAudioSystem.getInstance().playAudio(audioHolder, source);
    }

}
