package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.player.Player;

import java.util.HashMap;
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
    private Array<SpatialAudio> activeSounds;
    private Array<SpatialAudio> needRemoved;

    // 走路音效缓存：每种方块类型的走路音效只创建一次 SpatialAudio，切换方块时 pause/play 而不是 stop+new
    private final HashMap<AudioHolder, SpatialAudio> walkAudioCache = new HashMap<>();
    private AudioHolder curWalkAudio;
    private SpatialAudio curWalkAudioInstance;

    public SoundSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
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

    @Override
    public void dispose () {
        this.muteWalkSound();
        this.walkAudioCache.values().forEach(SpatialAudio::stop);
        this.walkAudioCache.clear();
        this.activeSounds.forEach(SpatialAudio::stop);
        this.activeSounds.clear();
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
            Vector2 playerCenter = player.getCenterPos();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            //脚下方块的材质变了才换音效，同一种地面连续走不重播
            AudioHolder walkAudio = underfootBlock.getProperty().getSounds().walk();
            if (! Objects.equals(this.curWalkAudio, walkAudio)) {
                this.muteWalkSound();
                this.playWalkSound(underfootBlock, player);
            }
            // 更新声源位置（玩家在移动）
            if (this.curWalkAudioInstance != null) {
                this.curWalkAudioInstance.setBoundSource(player.getSounder());
            }
        } else if (this.curWalkAudio != null) {
            this.muteWalkSound();
            this.curWalkAudio = null;
        }
    }

    /**
     * 播放/恢复方块类（方块 / 墙体 / 附着物）的走路音效
     * <p>
     * 三者都继承 Block，音效都挂在 Block.Property 的 BlockSounds 上，所以统一从这三个方法播放
     * @param block 脚下的方块类
     * @param walker 走路的实体
     * */
    public SpatialAudio playWalkSound (Block block, Entity<?> walker) {
        return this.playWalkSound(block.getProperty().getSounds().walk(), walker);
    }

    /**
     * 播放/恢复走路音效（优先复用缓存）
     * */
    private SpatialAudio playWalkSound (AudioHolder walkAudio, Entity<?> walker) {
        SpatialAudio audio = this.walkAudioCache.get(walkAudio);
        if (audio == null || !audio.isPlaying()) {
            audio = this.playSpatialSound(walkAudio, walker);
            audio.setLooping(true);
            this.walkAudioCache.put(walkAudio, audio);
        } else {
            audio.setBoundSource(walker.getSounder());
            audio.setVolume(1f);
        }
        this.curWalkAudio = walkAudio;
        this.curWalkAudioInstance = audio;
        return audio;
    }

    /**
     * 播放方块类（方块 / 墙体 / 附着物）的放置音效
     * @param block 被放置的方块类
     * */
    public SpatialAudio playPutSound (Block block, float wx, float wy) {
        return this.playSpatialSound(block.getProperty().getSounds().put(), this.atPosition(wx, wy));
    }

    /**
     * 播放方块类（方块 / 墙体 / 附着物）的破坏音效
     * @param block 被破坏的方块类
     * */
    public SpatialAudio playDestroySound (Block block, float wx, float wy) {
        return this.playSpatialSound(block.getProperty().getSounds().destroy(), this.atPosition(wx, wy));
    }

    /**
     * 一次性世界坐标音源（每次的破坏/放置位置都不同，不复用）
     * */
    private SpatialAudioSource atPosition (float wx, float wy) {
        Vector3 pos = new Vector3(wx, wy, 0);
        return () -> pos;
    }

    /**
     * 静音当前的走路音效（不停止播放，避免引擎 dispose）
     * */
    public void muteWalkSound () {
        if (this.curWalkAudioInstance != null) {
            this.curWalkAudioInstance.setVolume(0f);
            this.curWalkAudioInstance = null;
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
        SpatialAudio audio = SpatialAudioSystem.getInstance().playAudio(audioHolder, source);
        this.activeSounds.add(audio);
        return audio;
    }

}
